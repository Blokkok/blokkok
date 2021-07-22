package com.blokkok.app.processors

import android.os.Build
import com.blokkok.app.processors.compilers.ECJCompiler
import com.blokkok.app.processors.dexers.D8Dexer
import com.blokkok.app.processors.dexers.DxDexer
import com.blokkok.app.processors.signers.AndroidApkSigner

object ProcessorPicker {
    fun pickDexer(): Dexer {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> D8Dexer
            else -> DxDexer
        }
    }

    fun pickCompiler(): JavaCompiler = ECJCompiler

    fun pickSigner(): ApkSigner = AndroidApkSigner
}