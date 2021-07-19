package com.blokkok.app.processors

import android.content.Context
import java.io.File

interface Dexer {
    val name: String

    fun initialize(context: Context)
    suspend fun dex(
        folderOrFile: File,
        output: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
        classpaths: Array<File>? = null,
    ): Int // return value
}