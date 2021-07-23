package com.blokkok.app.managers.libraries

import kotlinx.serialization.Serializable

@Serializable
data class Library(
    val name: String,
    val type: LibraryType,
    val packageName: String? = null,     // this will be null when the type is NOT_CACHED
    val aarPath: String? = null,         // this will be null when the type is PRECOMPILED
    val cacheFolderPath: String? = null, // this will be null when the type is NOT_CACHED
)