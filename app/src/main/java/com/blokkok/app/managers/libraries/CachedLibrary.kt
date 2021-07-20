package com.blokkok.app.managers.libraries

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class CachedLibrary(
    val name: String,
    val packageName: String,
    val aarPath: String,
    val cachePath: String,
) {
    val classesDex: File        get() = File(cachePath).resolve("classes.jar")
    val classesBytecode: File   get() = File(cachePath).resolve("classes_bytecode.jar")
    val compiledResources: File get() = File(cachePath).resolve("res.zip")
}
