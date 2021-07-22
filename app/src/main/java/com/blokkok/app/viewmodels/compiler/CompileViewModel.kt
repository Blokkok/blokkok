package com.blokkok.app.viewmodels.compiler

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sdklib.build.ApkBuilder
import com.blokkok.app.managers.CommonFilesManager
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.processors.ApkSigner
import com.blokkok.app.processors.Dexer
import com.blokkok.app.processors.JavaCompiler
import com.blokkok.app.processors.ProcessorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.*

class CompileViewModel : ViewModel() {
    private val outputLiveDataMutable = MutableLiveData<String>()

    val outputLiveData: LiveData<String> = outputLiveDataMutable

    private suspend fun log(message: String) {
        withContext(Dispatchers.Main) { outputLiveDataMutable.value += "\n$message" }
    }

    fun compileProject(project: ProjectMetadata, context: Context) {

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
            val unalignedOutApk = File(cacheFolder, "${project.name}-unaligned-unsigned.apk") // unaligned and unaligned output apk
            val unsignedOutApk = File(cacheFolder, "${project.name}-unsigned.apk") // unsigned output apk
            val outApk = File(cacheFolder, "${project.name}.apk") // signed output apk

            // mkdirs ==============================================================================
            cacheFolder.mkdirs()

            classesCacheFolder.mkdirs()
            generatedJavaFolder.mkdirs()
            dexCacheFolder.mkdirs()
            compiledResCacheFolder.mkdirs()
            // mkdirs ==============================================================================

            // Picks the compile, dexer and the signer
            val compiler = ProcessorPicker.pickCompiler()
            val dexer = ProcessorPicker.pickDexer()
            val signer = ProcessorPicker.pickSigner()

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

            log("\nStarting to build APK")

            val apkBuilder = ApkBuilder(
                unalignedOutApk,
                resOutApk,
                classesDex,
                null, // No key and no cert, we will sign this using ApkSigner instead
                null,
                PrintStream(OutputStreamLogger("ApkBuilder >> "))
            )
            apkBuilder.setDebugMode(false)
            apkBuilder.sealApk()

            // =====================================================================================
            // Second to last, zipalign the apk
            val zipalignRetVal = zipalignApk(unalignedOutApk, unsignedOutApk)

            if (zipalignRetVal != 0) {
                log("zipalign returned a non-zero status"); return@launch
            } else {
                log("zipaligned has finished aligning the apk")
            }

            // =====================================================================================
            // Then finally, sign the apk using apksigner with the debug key
            // Note: zipalign must be run before the app is signed using apksigner, but if you use
            //       jarsigner, zipalign must be run after that
            val signerRetVal = signApk(signer, unsignedOutApk, outApk)

            if (signerRetVal != 0) {
                log("${signer.name} returned a non-zero status"); return@launch
            } else {
                log("${signer.name} has finished signing the apk")
            }

            log("\nThe app has been successfully built!")
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
                arrayOf(
                    "link",
                    "-I", androidJar.absolutePath,
                    "--auto-add-overlay",
                    "--allow-reserved-package-id",
                    "--no-version-vectors",
                    "--min-sdk-version", "21", // TODO: 7/20/21 add these to the project metadata
                    "--target-sdk-version", "30",
                    "--version-code", "1",
                    "--version-name", "1.0",
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

    private suspend fun zipalignApk(
        inputApkFile: File,
        outputApkFile: File,
    ): Int {
        log("\nZipalign is aligning the apk")

        return NativeBinariesManager
            .executeCommand(
                NativeBinariesManager.NativeBinaries.ZIP_ALIGN,
                arrayOf("-v", "4", inputApkFile.absolutePath, outputApkFile.absolutePath),
                { runBlocking { log("Zipalign >> $it") } },
                { runBlocking { log("Zipalign ERR >> $it") } }
            )
    }

    private suspend fun signApk(
        signer: ApkSigner,
        inputApkFile: File,
        outputApkFile: File,
        privateKey: File = CommonFilesManager.testKeyPrivateKey,
        publicKey: File = CommonFilesManager.testKeyPublicKey,
    ): Int {
        log("\n${signer.name} is signing the apk")

        return signer.sign(inputApkFile, outputApkFile, privateKey, publicKey,
            { runBlocking { log("${signer.name} >> $it") } },
            { runBlocking { log("${signer.name} ERR >> $it") } }
        )
    }

    fun compileLibrary(libraryName: String) {
        viewModelScope.launch {
            log("Starting to compile library $libraryName")

            val retVal = LibraryManager.compileLibrary(
                libraryName,
                { runBlocking { log(it) } },
                { runBlocking { log(it) } }
            )

            if (retVal == 100) {
                log("Library $libraryName doesn't exist")
            } else if (retVal != 0) {
                log("Compiler returned non-zero status")
            }

            log("Compiling finished")
        }
    }

    inner class OutputStreamLogger(
        private val prefix: String
    ) : OutputStream() {

        private val buffer = StringBuilder()

        override fun write(b: Int) {
            val char = b.toChar()

            if (char == '\n') {
                runBlocking { log("$prefix$buffer") }
                buffer.clear()
            } else {
                buffer.append(char)
            }
        }
    }
}