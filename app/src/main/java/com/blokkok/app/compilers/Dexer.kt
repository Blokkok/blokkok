package com.blokkok.app.compilers

import android.content.Context
import java.io.File
import java.io.PrintWriter

interface Dexer {
    fun initialize(context: Context)
    suspend fun dex(
        rootPackageFolder: File,
        output: File,
        stdout: PrintWriter,
        stderr: PrintWriter,
    ): Int // return value
}