package com.blokkok.app.ui.viewmodels

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class LicensesViewModel : ViewModel() {
    private val licenseTextMutable = MutableLiveData("")
    val licenseText: LiveData<String> = licenseTextMutable

    fun loadLicense(assets: AssetManager) {
        viewModelScope.launch(Dispatchers.IO) {
            val licenseText = String(
                assets
                    .open("LICENSE")
                    .readBytes(),
                StandardCharsets.UTF_8
            )

            withContext(Dispatchers.Main) { licenseTextMutable.value = licenseText }
        }
    }
}