package com.blokkok.app.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.managers.libraries.Library
import com.blokkok.app.managers.libraries.LibraryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class LibrariesViewModel : ViewModel()  {
    private val librariesMutable = MutableLiveData<Array<Library>>()

    val libraries: LiveData<Array<Library>> = librariesMutable

    fun loadLibraries() {
        viewModelScope.launch(Dispatchers.IO) {
            val libraries = LibraryManager.listLibraries().toTypedArray()
            withContext(Dispatchers.Main) { librariesMutable.value = libraries }
        }
    }

    fun addAARLibrary(inputStream: InputStream, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            LibraryManager.addAARLibrary(inputStream, name)
            loadLibraries()
        }
    }

    fun addPrecompiledLibrary(zipFile: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            LibraryManager.addPrecompiledLibrary(zipFile)
            loadLibraries()
        }
    }
}