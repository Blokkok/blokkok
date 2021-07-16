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
                ECJCompiler.compileJava(javaFiles, classesCacheFolder,
                    PrintWriter(object : Writer() {
                        override fun close() {}
                        override fun flush() {}

                        override fun write(cbuf: CharArray?, off: Int, len: Int) {
                            viewModelScope.launch(Dispatchers.Main) {
                                outputLiveDataMutable.value += "\nECJ >> ${cbuf?.let { String(it) }}"
                            }
                        }
                    }),
                    PrintWriter(object : Writer() {
                        override fun close() {}
                        override fun flush() {}

                        override fun write(cbuf: CharArray?, off: Int, len: Int) {
                            viewModelScope.launch(Dispatchers.Main) {
                                outputLiveDataMutable.value += "\nECJ ERR >> ${cbuf?.let { String(it) }}"
                            }
                        }
                    })
                )
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
                D8Dexer.dex(classesCacheFolder, dexCacheFolder,
                    PrintWriter(object : Writer() {
                        override fun close() {}
                        override fun flush() {}

                        override fun write(cbuf: CharArray?, off: Int, len: Int) {
                            viewModelScope.launch(Dispatchers.Main) {
                                outputLiveDataMutable.value += "\nD8 >> ${cbuf?.let { String(it) }}"
                            }
                        }
                    }),
                    PrintWriter(object : Writer() {
                        override fun close() {}
                        override fun flush() {}

                        override fun write(cbuf: CharArray?, off: Int, len: Int) {
                            viewModelScope.launch(Dispatchers.Main) {
                                outputLiveDataMutable.value += "\nD8 ERR >> ${cbuf?.let { String(it) }}"
                            }
                        }
                    })
                )
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