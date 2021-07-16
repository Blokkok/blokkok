package com.blokkok.app.compilers

import android.os.Build

object CompilerPicker {
    fun pickDexer(): Dexer {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> D8Dexer
            else -> DxDexer
        }
    }

    fun pickCompiler(): JavaCompiler = ECJCompiler
}