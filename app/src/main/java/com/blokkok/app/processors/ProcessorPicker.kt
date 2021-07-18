package com.blokkok.app.processors

import android.os.Build
import com.blokkok.app.processors.dexers.D8Dexer
import com.blokkok.app.processors.dexers.DxDexer

object ProcessorPicker {
    fun pickDexer(): Dexer {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> D8Dexer
            else -> DxDexer
        }
    }

    fun pickCompiler(): JavaCompiler = ECJCompiler
}