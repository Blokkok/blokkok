package com.blokkok.app.managers.projects

import android.content.Context
import kotlinx.serialization.Serializable
import java.io.File

// meta.json for projects/
@Serializable
data class ProjectMetadata(
    val name: String,
    val packageName: String,
    val id: String,
    val libraries: List<String>, // List of library names used in this project
    // TODO: 7/11/21 Modules
) {
    fun edit(context: Context) =
        ProjectEditor(File(context.applicationInfo.dataDir, "projects/$id/data/"))
}
