package com.blokkok.app.processors.signers

import android.content.Context
import com.blokkok.app.processors.ApkSigner
import java.io.*

object AndroidApkSigner : ApkSigner {
    override val name: String = "ApkSigner"

    private lateinit var apkSignerDir: File
    private lateinit var apkSignerJar: File

    override fun initialize(context: Context) {
        apkSignerDir = File(context.applicationInfo.dataDir, "binaries/apksigner")
        apkSignerJar = File("${apkSignerDir.absolutePath}/apksigner.jar")

        if (!apkSignerDir.exists()) {
            apkSignerDir.mkdirs()
            extract(context)
        }
    }

    private fun extract(context: Context) {
        val writer: OutputStream
        val bufSize = 8 * 1024

        val bis = BufferedInputStream(context.assets.open("apksigner/apksigner.jar"))
        writer = BufferedOutputStream(FileOutputStream(apkSignerJar))
        val buf = ByteArray(bufSize)
        var len: Int

        while (bis.read(buf, 0, bufSize).also { len = it } > 0) { writer.write(buf, 0, len) }

        writer.close()
        bis.close()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun sign(
        apkFile: File,
        outputApk: File,
        privateKey: File,
        publicKey: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int {
        val process = Runtime.getRuntime().exec(
            "dalvikvm -Xmx256m -cp ${apkSignerJar.absolutePath} com.android.apksigner.ApkSignerTool sign --in ${apkFile.absolutePath} --out ${outputApk.absolutePath} --key ${privateKey.absolutePath} --cert ${publicKey.absolutePath} -v"
        )

        process.inputStream.redirectTo(stdout)
        process.errorStream.redirectTo(stderr)

        return process.waitFor()
    }
}

private fun InputStream.redirectTo(out: (String) -> Unit) {
    Thread {
        BufferedReader(InputStreamReader(this)).also { reader ->
            reader.forEachLine { out(it) }
        }.close()
    }.run()
}