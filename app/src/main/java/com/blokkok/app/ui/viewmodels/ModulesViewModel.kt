package com.blokkok.app.ui.viewmodels

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blokkok.modsys.ModuleManager
import com.blokkok.modsys.models.ModuleMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.zip.ZipInputStream

class ModulesViewModel(application: Application) : AndroidViewModel(application) {

    private val modulesMutable = MutableLiveData<List<ModuleMetadata>>()
    private val toastActionMutable = MutableLiveData<String>()
    private val loadStatusMutable = MutableLiveData<String>()
    private val loadingModulesStatusMutable = MutableLiveData<Boolean>()

    val toastAction: LiveData<String> = toastActionMutable
    val modules: LiveData<List<ModuleMetadata>> = modulesMutable
    val loadStatus: LiveData<String> = loadStatusMutable
    val loadingModulesStatus: LiveData<Boolean> = loadingModulesStatusMutable

    fun importModule(contentResolver: ContentResolver, uri: Uri) {
        val descriptor = contentResolver.openAssetFileDescriptor(uri, "r")!!
        val inputStream = descriptor.createInputStream()

        viewModelScope.launch(Dispatchers.IO) {
            ModuleManager.importModule(ZipInputStream(inputStream))

            listModules()

            withContext(Dispatchers.Main) {
                toastActionMutable.value = "Module has been successfully imported"
            }
        }
    }

    fun listModules() {
        viewModelScope.launch {
            modulesMutable.value = ModuleManager.listModules().values.toList()
        }
    }

    fun loadModules() {
        loadingModulesStatusMutable.value = true
        loadStatusMutable.value = "Loading modules"

        viewModelScope.launch(Dispatchers.Unconfined) {
            ModuleManager.loadModules({
                toastActionMutable.value = it
            }, (getApplication() as Application).codeCacheDir.absolutePath)

            withContext(Dispatchers.Main) {
                loadingModulesStatusMutable.value = false
                loadStatusMutable.value = "Modules loaded"
                toastActionMutable.value = "Modules has been successfully loaded!"
            }
        }
    }

    fun unloadModules() {
        loadingModulesStatusMutable.value = true
        loadStatusMutable.value = "Unloading modules"

        viewModelScope.launch(Dispatchers.Unconfined) {
            ModuleManager.unloadModules()

            withContext(Dispatchers.Main) {
                loadingModulesStatusMutable.value = false
                loadStatusMutable.value = "Modules unloaded"
                toastActionMutable.value = "Modules has been successfully unloaded!"
            }
        }
    }
}