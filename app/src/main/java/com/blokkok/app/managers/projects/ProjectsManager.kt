package com.blokkok.app.managers.projects

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ProjectsManager {

    private lateinit var dataDir: File
    private lateinit var projects: File

    fun initialize(context: Context) {
        dataDir = File(context.applicationInfo.dataDir)
        projects = File(dataDir, "projects")

        // Check if the projects folder exists
        if (!projects.exists()) projects.mkdir()
    }

    fun listProjects(): List<ProjectMetadata> =
        projects.listFiles()!!.map { file ->
            Json.decodeFromString(File(file, "meta.json").readText())
        }

    fun exists(id: String): Boolean = listProjects().any { it.id == id }

    fun getProject(id: String): ProjectMetadata? {
        if (!exists(id)) return null

        return Json.decodeFromString(File(projects, "$id/meta.json").readText())
    }

    fun removeProject(id: String): Boolean {
        if (!exists(id)) return false
        return File(projects, id).deleteRecursively()
    }

    fun clearProjects() {
        listProjects().forEach { removeProject(it.id) }
    }

    fun createProject(name: String, packageName: String) {
        val id = generateRandomId()
        val metadata = ProjectMetadata(name, packageName, id)
        val projectDir = File(projects, id)

        projectDir.mkdir()

        File(projectDir, "android").mkdir()     // Files required by the compiler
        File(projectDir, "data").mkdir()        // Project files
        File(projectDir, "cache").mkdir()       // Cache folder

        File(projectDir, "meta.json").writeText(Json.encodeToString(metadata))
    }

    private fun generateRandomId(): String {
        val projects = listProjects()
        var id: String

        do {
            id = List(16) {
                (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
            }.joinToString("")

        } while (projects.any { it.id == id }) // This checks if the generated id already exists in the projects list

        return id
    }
}