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
    // TODO: 7/11/21 Modules
) {
    fun edit(context: Context) =
        ProjectEditor(File(context.applicationInfo.dataDir, "projects/$id/"))
}
