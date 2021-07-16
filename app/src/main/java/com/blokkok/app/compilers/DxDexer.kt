package com.blokkok.app.compilers

import android.content.Context
import java.io.File

object DxDexer : Dexer {
    override fun initialize(context: Context) {
        TODO("Dx is not yet implemented")
    }

    override suspend fun dex(
        rootPackageFolder: File,
        output: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int {
        TODO("Dx is not yet implemented")
    }
}