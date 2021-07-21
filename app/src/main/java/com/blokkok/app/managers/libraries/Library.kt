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

/**
 * This class is used to contain a list of libraries since kotlin's serialization library
 * doesn't support one value array json
 */
@Serializable
data class LibraryContainer(val libraries: ArrayList<Library>)