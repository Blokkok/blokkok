package com.blokkok.app.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blokkok.app.adapters.LibraryItem
import com.blokkok.app.managers.libraries.LibraryManager

class LibrariesViewModel : ViewModel()  {
    private val librariesMutable = MutableLiveData<Array<LibraryItem>>()

    val libraries: LiveData<Array<LibraryItem>> = librariesMutable

    fun loadLibraries() {
        librariesMutable.value =
            LibraryManager
                .listLibraries()
                .map { LibraryItem(it, LibraryManager.isCached(it)) }
                .toTypedArray()
    }
}