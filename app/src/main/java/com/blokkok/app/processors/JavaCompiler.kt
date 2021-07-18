package com.blokkok.app.processors

import android.content.Context
import java.io.File

interface JavaCompiler {

    val name: String

    fun initialize(context: Context)
    suspend fun compileJava(
        inputFolders: Array<File>,
        outputFolder: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
    ): Int // return value
}