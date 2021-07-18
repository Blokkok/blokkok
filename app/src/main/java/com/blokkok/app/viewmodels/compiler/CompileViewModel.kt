package com.blokkok.app.viewmodels.compiler

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.compilers.CompilerPicker
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

        viewModelScope.launch(Dispatchers.IO) {
            val javaFiles = File(context.applicationInfo.dataDir, "projects/${project.id}/data/java/${project.packageName.split(".")[0]}/")
            val cacheFolder = File(context.applicationInfo.dataDir, "projects/${project.id}/cache/")

            val classesCacheFolder = File(cacheFolder, "classes")
            val dexCacheFolder = File(cacheFolder, "dex")
            val classesDex = File(dexCacheFolder, "classes.jar")

            classesCacheFolder.mkdirs()
            dexCacheFolder.mkdirs()

            val compiler = CompilerPicker.pickCompiler()
            val dexer = CompilerPicker.pickDexer()

            // Run ecj

            log("ECJ is starting to compile")

            val ecjRetValue = withContext(Dispatchers.IO) {
                compiler.compileJava(arrayOf(javaFiles, generatedJavaFolder), classesCacheFolder,
                    { runBlocking { log("ECJ >> $it") } },
                    { runBlocking { log("ECJ ERR >> $it") } }
                )
            }

            if (ecjRetValue != 0) {
                // this is not good, ecj returned a non-zero status (something goes wrong)
                log("ECJ returned a non-zero status")
                return@launch

            } else {
                log("ECJ has finished compiling")
            }

            log("\nD8 is starting to dex")

            // Continue with d8
            val d8RetValue = withContext(Dispatchers.IO) {
                dexer.dex(classesCacheFolder, classesDex,
                    { runBlocking { log("D8 >> $it") } },
                    { runBlocking { log("D8 ERR >> $it") } }
                )
            }

            if (d8RetValue != 0) {
                log("D8 returned a non-zero status")
                return@launch

            } else {
                log("D8 has finished dex-ing")
            }

            log("\nAAPT2 starting to run")

            val aapt2RetVal = NativeBinariesManager
                .executeCommand(
                    NativeBinariesManager.NativeBinaries.AAPT2,
                    arrayOf("link"),
                    { runBlocking { log("AAPT2 >> $it") } },
                    { runBlocking { log("AAPT2 ERR >> $it") } }
                )

            if (aapt2RetVal != 0) {
                log("aapt2 returned a non-zero status")
                return@launch

            } else {
                log("aapt2 has finished running")
            }
        }
    }
}