package com.blokkok.app.processors

import android.content.Context
import java.io.File

interface ApkSigner {
    val name: String

    fun initialize(context: Context)
    suspend fun sign(
        apkFile: File,
        outputApk: File,
        privateKey: File,
        publicKey: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
    ): Int // return value
}