package com.blokkok.app.compilers

import android.content.Context
import java.io.File
import java.io.PrintWriter

interface JavaCompiler {
    fun initialize(context: Context)
    suspend fun compileJava(
        rootPackageFolder: File,
        outputFolder: File,
        stdout: PrintWriter,
        stderr: PrintWriter,
    ): Int // return value
}