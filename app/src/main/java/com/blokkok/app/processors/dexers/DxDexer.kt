package com.blokkok.app.processors.dexers

import android.content.Context
import com.blokkok.app.processors.Dexer
import java.io.File

object DxDexer : Dexer {
    override val name: String get() = "Dx"

    override fun initialize(context: Context) {
        TODO("Dx is not yet implemented")
    }

    override suspend fun dex(
        folderOrFile: File,
        output: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int {
        TODO("Dx is not yet implemented")
    }
}