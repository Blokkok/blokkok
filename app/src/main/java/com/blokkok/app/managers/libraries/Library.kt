package com.blokkok.app.managers.libraries

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Library(
    val name: String,
    val type: LibraryType,
    val packageName: String? = null,     // this will be null when the type is NOT_CACHED
    val aarPath: String? = null,         // this will be null when the type is PRECOMPILED
    val cacheFolderPath: String? = null, // this will be null when the type is NOT_CACHED
) {
    val classesJar: File?          get() = cacheFolderPath?.let { File(it, "classes.jar") }
    val classesDexes: Array<File>? get() = cacheFolderPath?.let { File(it, "dex").listFiles()!! }
    val compiledResources: File?   get() = cacheFolderPath?.let { File(it, "res.zip") }
}