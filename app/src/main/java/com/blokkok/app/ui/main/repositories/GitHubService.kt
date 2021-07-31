package com.blokkok.app.ui.main.repositories

import com.blokkok.app.ui.main.adapters.GitHubUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

// Used to fetch github-related stuff
object GitHubService {

    // Blocking on IO is okay, this warning is false-positive
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun fetchData(url: String): String =
        withContext(Dispatchers.IO) {
            URL(url).openStream().use {
                it.bufferedReader().readText()
            }
        }

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getContributors(repoOwner: String, repoName: String): List<GitHubUser> =
        withContext(Dispatchers.IO) {
            jsonConfig.decodeFromString(
                fetchData("https://api.github.com/repos/$repoOwner/$repoName/contributors")
            )
        }

    suspend fun getOrgMembers(orgName: String): List<GitHubUser> =
        withContext(Dispatchers.IO) {
            jsonConfig.decodeFromString(
                fetchData("https://api.github.com/orgs/$orgName/members")
            )
        }
}