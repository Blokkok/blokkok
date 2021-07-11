package com.blokkok.app.managers.projects

import kotlinx.serialization.Serializable

// meta.json for projects/
@Serializable
data class ProjectMetadata(
    val name: String,
    val packageName: String,
    val id: String,
    // TODO: 7/11/21 Modules
)
