package com.blokkok.app.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blokkok.app.managers.libraries.Library
import com.blokkok.app.managers.libraries.LibraryManager

class LibrariesViewModel : ViewModel()  {
    private val librariesMutable = MutableLiveData<Array<Library>>()

    val libraries: LiveData<Array<Library>> = librariesMutable

    fun loadLibraries() {
        librariesMutable.value =
            LibraryManager
                .listLibraries().toTypedArray()
    }
}