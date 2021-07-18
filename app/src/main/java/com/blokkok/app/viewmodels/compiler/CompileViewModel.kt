package com.blokkok.app.viewmodels.compiler

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.processors.ProcessorPicker
import com.blokkok.app.processors.Dexer
import com.blokkok.app.processors.JavaCompiler
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.managers.projects.ProjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class CompileViewModel : ViewModel() {
    private val outputLiveDataMutable = MutableLiveData<String>()

    val outputLiveData: LiveData<String> = outputLiveDataMutable

    private suspend fun log(message: String) {
        withContext(Dispatchers.Main) { outputLiveDataMutable.value += "\n$message" }
    }

    fun startCompilation(project: ProjectMetadata, context: Context) {

        val dataDir = context.applicationInfo.dataDir

        val androidJar = File("$dataDir/binaries/android.jar") // android.jar file
        val packagePath = project.packageName.replace(".", "/") // The path of the package
        val firstPackage = project.packageName.split(".")[0] // First item in the package

        val javaFiles =
            File(dataDir, "projects/${project.id}/data/java/$firstPackage/")

        val resFolder =
            File(dataDir, "projects/${project.id}/data/res")

        val androidManifestXml =
            File(dataDir, "projects/${project.id}/data/AndroidManifest.xml")

        val cacheFolder =
            File(dataDir, "projects/${project.id}/cache/")


        viewModelScope.launch(Dispatchers.IO) {
            val classesCacheFolder      = File(cacheFolder, "classes") // where compiled java files from both generated java files and the project's code are located
            val generatedJavaFolder     = File(cacheFolder, "genJava") // where java generated files like R.java are located
            val dexCacheFolder          = File(cacheFolder, "dex")     // where the dex-ed class files from the classes folder are located
            val compiledResCacheFolder  = File(cacheFolder, "compiledRes") // where the compiled resources files (resources.zip) are located

            val resourcesZip = File(compiledResCacheFolder, "resources.zip") // the compiled resources
            val classesDex = File(dexCacheFolder, "classes.jar") // dex-ed classes from both generated java files and project's code
            val resOutApk = File(cacheFolder, "res.apk") // output apk with resources

            // mkdirs ==============================================================================
            cacheFolder.mkdirs()

            classesCacheFolder.mkdirs()
            generatedJavaFolder.mkdirs()
            dexCacheFolder.mkdirs()
            compiledResCacheFolder.mkdirs()
            // mkdirs ==============================================================================

            // Picks the compiler and the dexer
            val compiler = ProcessorPicker.pickCompiler()
            val dexer = ProcessorPicker.pickDexer()

            ///////////////////////
            // Start compilation //
            ///////////////////////

            // =====================================================================================
            // First, run aapt2 to compile resources

            val aapt2crRetVal = compileResources(
                resFolder, // the res folder
                resourcesZip // and the output file will be cache/compiledRes/resources.zip
            )

            if (aapt2crRetVal != 0) {
                log("aapt2 returned a non-zero status"); return@launch
            } else {
                log("aapt2 has finished compiling resources")
            }

            // =====================================================================================
            // Then, link the resources with aapt2

            val aapt2lrRetVal = linkResources(
                androidJar, // the android.jar file
                androidManifestXml, // the AndroidManifest.xml file
                File(generatedJavaFolder, packagePath), // output folder of R.java
                resOutApk, // output apk
                resourcesZip // the compiled resources
            )

            if (aapt2lrRetVal != 0) {
                log("aapt2 returned a non-zero status"); return@launch
            } else {
                log("aapt2 has finished linking resources")
            }

            // =====================================================================================
            // Then run the java compiler to compile java sources

            val compilerRetVal = compileJavaSources(
                compiler, // the compiler used
                arrayOf(generatedJavaFolder, javaFiles), // the java sources that'll be compiled
                classesCacheFolder // the output folder
            )

            if (compilerRetVal != 0) {
                log("${compiler.name} returned a non-zero status"); return@launch
            } else {
                log("${compiler.name} has finished compiling")
            }

            // =====================================================================================
            // Continue with the dexer to dex those compiled java files

            val dexerRetVal = dexClasses(
                dexer, // the dexer used
                classesCacheFolder, // the classes that'll be dex-ed by the dexer
                classesDex // the output dex file
            )

            if (dexerRetVal != 0) {
                log("${dexer.name} returned a non-zero status"); return@launch
            } else {
                log("${dexer.name} has finished dex-ing")
            }

            // =====================================================================================
            // Then build the apk using ApkBuilder
        }
    }

    private suspend fun compileJavaSources(
        compiler: JavaCompiler,
        inputFolders: Array<File>,
        outputFolder: File
    ): Int {
        log("\n${compiler.name} is starting to compile")

        return compiler.compileJava(inputFolders, outputFolder,
                { runBlocking { log("${compiler.name} >> $it") } },
                { runBlocking { log("${compiler.name} ERR >> $it") } }
            )
    }

    private suspend fun dexClasses(
        dexer: Dexer,
        compiledClassesFolder: File,
        outputDex: File
    ): Int {
        log("\n${dexer.name} is starting to dex")

        return dexer.dex(compiledClassesFolder, outputDex,
                { runBlocking { log("${dexer.name} >> $it") } },
                { runBlocking { log("${dexer.name} ERR >> $it") } }
            )
    }

    private suspend fun compileResources(resFolder: File, resourcesZipOutput: File): Int {
        log("\nAAPT2 is compiling resources")

        return NativeBinariesManager
            .executeCommand(
                NativeBinariesManager.NativeBinaries.AAPT2,
                arrayOf("compile", "--dir", resFolder.absolutePath, "-o", resourcesZipOutput.absolutePath, "-v"),
                { runBlocking { log("AAPT2 >> $it") } },
                { runBlocking { log("AAPT2 ERR >> $it") } }
            )
    }

    private suspend fun linkResources(
        androidJar: File,
        androidManifestXml: File,
        rJavaOutput: File,
        outputApk: File,
        resourcesZip: File
    ): Int {
        log("\nAAPT2 is linking resources")

        return NativeBinariesManager
            .executeCommand(
                NativeBinariesManager.NativeBinaries.AAPT2,
                arrayOf("link",
                    "-I", androidJar.absolutePath,
                    "--auto-add-overlay",
                    "--manifest", androidManifestXml.absolutePath,
                    "--java", rJavaOutput.absolutePath,
                    "-o", outputApk.absolutePath,
                    resourcesZip.absolutePath,
                    "-v"
                ),
                { runBlocking { log("AAPT2 >> $it") } },
                { runBlocking { log("AAPT2 ERR >> $it") } }
            )
    }
}