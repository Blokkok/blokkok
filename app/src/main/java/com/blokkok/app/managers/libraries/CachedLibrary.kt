package com.blokkok.app.managers.libraries

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class CachedLibrary(
    val name: String,
    val packageName: String,

    /** these values are assigned on [LibraryManager],
      * don't assign them when you wanted to serialize this
      */
    // FIXME: 7/20/21 Is there any way to make the serializer ignore a value when serializing while maintaining it's non-nullable
    var aarPath: String? = null,
    var cachePath: String? = null,
) {
    val classesDexFiles: Array<File> get() = File(cachePath!!).resolve("dex").listFiles()!!
    val classesBytecode: File        get() = File(cachePath!!).resolve("classes_bytecode.jar")
    val compiledResources: File      get() = File(cachePath!!).resolve("res.zip")
}
