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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.io.Writer

class CompileViewModel : ViewModel() {
    private val outputLiveDataMutable = MutableLiveData<String>()

    val outputLiveData: LiveData<String> = outputLiveDataMutable

    fun startCompilation(project: ProjectMetadata, context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            val javaFiles = File(context.applicationInfo.dataDir, "projects/${project.id}/data/java/${project.packageName.split(".")[0]}/")
            val cacheFolder = File(context.applicationInfo.dataDir, "projects/${project.id}/cache/")

            val classesCacheFolder = File(cacheFolder, "classes")
            val dexCacheFolder = File(cacheFolder, "dex")

            // Run ecj

            withContext(Dispatchers.Main) {
                outputLiveDataMutable.value += "\nECJ has started compiling"
            }

            val ecjRetValue = withContext(Dispatchers.IO) {
                ECJCompiler.compileJava(javaFiles, classesCacheFolder, {
                    viewModelScope.launch(Dispatchers.Main) { // stdout
                        outputLiveDataMutable.value += "\nECJ >> $it"
                    }}, {
                    viewModelScope.launch(Dispatchers.Main) { // stderr
                        outputLiveDataMutable.value += "\nECJ ERR >> $it"
                    }
                })
            }

            if (ecjRetValue != 0) {
                // this is not good, ecj returned a non-zero status (something goes wrong)
                withContext(Dispatchers.Main) {
                    outputLiveDataMutable.value += "\nECJ returned a non-zero status"
                }

                return@launch
            } else {
                withContext(Dispatchers.Main) {
                    outputLiveDataMutable.value += "\nECJ has finished compiling"
                }
            }

            // Continue with d8
            val d8RetValue = withContext(Dispatchers.IO) {
                D8Dexer.dex(classesCacheFolder, dexCacheFolder, {
                    viewModelScope.launch(Dispatchers.Main) { // stdout
                        outputLiveDataMutable.value += "\nD8 >> $it"
                    }}, {
                    viewModelScope.launch(Dispatchers.Main) { // stderr
                        outputLiveDataMutable.value += "\nD8 ERR >> $it"
                    }
                })
            }

            if (d8RetValue != 0) {
                // this is not good, ecj returned a non-zero status (something goes wrong)
                withContext(Dispatchers.Main) {
                    outputLiveDataMutable.value += "\nD8 returned a non-zero status"
                }

                return@launch
            } else {
                withContext(Dispatchers.Main) {
                    outputLiveDataMutable.value += "\nD8 has finished dex-ing"
                }
            }
        }
    }
}