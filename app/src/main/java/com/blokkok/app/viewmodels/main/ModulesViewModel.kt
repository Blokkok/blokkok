package com.blokkok.app.viewmodels.main

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.modsys.ModuleManager
import com.blokkok.modsys.models.ModuleMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.zip.ZipInputStream

class ModulesViewModel : ViewModel() {

    private val modulesMutable = MutableLiveData<List<ModuleMetadata>>()
    private val toastActionMutable = MutableLiveData<String>()

    val toastAction: LiveData<String> = toastActionMutable
    val modules: LiveData<List<ModuleMetadata>> = modulesMutable

    fun importModule(contentResolver: ContentResolver, uri: Uri) {
        val descriptor = contentResolver.openAssetFileDescriptor(uri, "r")!!
        val inputStream = descriptor.createInputStream()

        viewModelScope.launch(Dispatchers.IO) {
            ModuleManager.importModule(ZipInputStream(inputStream))

            loadModules()

            withContext(Dispatchers.Main) {
                toastActionMutable.value = "Module has been successfully imported"
            }
        }
    }

    fun loadModules() {
        viewModelScope.launch {
            modulesMutable.value = ModuleManager.listModules().values.toList()
        }
    }
}