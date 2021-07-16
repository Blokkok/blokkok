package com.blokkok.app.viewmodels.compiler

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.compilers.D8Dexer
import com.blokkok.app.compilers.ECJCompiler
import com.blokkok.app.managers.projects.ProjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.io.Writer

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

            // Run ecj

            log("ECJ has started compiling")

            val ecjRetValue = withContext(Dispatchers.IO) {
                ECJCompiler.compileJava(javaFiles, classesCacheFolder,
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

            // Continue with d8
            val d8RetValue = withContext(Dispatchers.IO) {
                D8Dexer.dex(classesCacheFolder, dexCacheFolder,
                    { runBlocking { log("D8 >> $it") } },
                    { runBlocking { log("D8 ERR >> $it") } }
                )
            }

            if (d8RetValue != 0) {
                // this is not good, ecj returned a non-zero status (something goes wrong)
                log("D8 returned a non-zero status")
                return@launch

            } else {
                log("D8 has finished dex-ing")
            }
        }
    }
}