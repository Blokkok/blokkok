package com.blokkok.app.ui.viewmodels

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.ui.adapters.GitHubUser
import com.blokkok.app.ui.repositories.GitHubService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class AboutViewModel : ViewModel() {

    private val contributorsMutable = MutableLiveData<List<GitHubUser>>()
    private val descriptionMutable = MutableLiveData<String>()
    private val membersMutable = MutableLiveData<List<GitHubUser>>()

    val contributors: LiveData<List<GitHubUser>> = contributorsMutable
    val description: LiveData<String> = descriptionMutable
    val members: LiveData<List<GitHubUser>> = membersMutable

    fun fetchContributors() {
        viewModelScope.launch {
            // TODO: 7/31/21 Replace these with Blokkok once it's open sourced
            contributorsMutable.value =
                GitHubService.getContributors(
                    "OpenBlocksTeam",
                    "openblocks-app"
                )
        }
    }

    fun fetchTeam() {
        viewModelScope.launch {
            // TODO: 7/31/21 Replace these with Blokkok once it's open sourced
            membersMutable.value =
                GitHubService.getOrgMembers(
                    "OpenBlocksTeam"
                )
        }
    }

    fun loadDescription(assets: AssetManager) {
        viewModelScope.launch(Dispatchers.IO) {
            val descriptionText = String(
                assets
                    .open("description")
                    .readBytes(),
                StandardCharsets.UTF_8
            )

            withContext(Dispatchers.Main) { descriptionMutable.value = descriptionText }
        }
    }
}