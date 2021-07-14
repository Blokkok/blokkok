package com.blokkok.app.viewmodels.compiler

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.compiler.ECJCompiler
import com.blokkok.app.managers.projects.ProjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

            withContext(Dispatchers.Main) {
                outputLiveDataMutable.value += "\nECJ has started compiling"
            }

            val ecjWait = async(Dispatchers.IO) {
                ECJCompiler.compile(javaFiles.absolutePath, cacheFolder.absolutePath,
                    PrintWriter(object : Writer() {
                        override fun close() {}
                        override fun flush() {}

                        override fun write(cbuf: CharArray?, off: Int, len: Int) {
                            viewModelScope.launch(Dispatchers.Main) {
                                outputLiveDataMutable.value += "\n>> ${cbuf?.let { String(it) }}"
                            }
                        }
                    }),
                    PrintWriter(object : Writer() {
                        override fun close() {}
                        override fun flush() {}

                        override fun write(cbuf: CharArray?, off: Int, len: Int) {
                            viewModelScope.launch(Dispatchers.Main) {
                                outputLiveDataMutable.value += "\nERR >> ${cbuf?.let { String(it) }}"
                            }
                        }
                    })
                )
            }

            val retValue = ecjWait.await()

            withContext(Dispatchers.Main) {
                if (retValue != 0) {
                    // this is not good, ecj returned a non-zero status (something goes wrong)
                    outputLiveDataMutable.value += "\nECJ returned a non-zero status"
                } else {
                    outputLiveDataMutable.value += "\nECJ has finished compiling"
                }
            }
        }
    }
}