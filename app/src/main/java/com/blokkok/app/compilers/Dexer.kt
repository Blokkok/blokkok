package com.blokkok.app.compilers

import android.content.Context
import java.io.File

interface Dexer {
    fun initialize(context: Context)
    suspend fun dex(
        rootPackageFolder: File,
        output: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
    ): Int // return value
}