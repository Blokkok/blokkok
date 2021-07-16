package com.blokkok.app.compiler

import android.content.Context
import java.io.*

object D8Dexer {
    private lateinit var d8Dir: File
    private lateinit var d8Path: String

    fun initialize(context: Context) {
        d8Dir = File(context.applicationInfo.dataDir, "binaries/d8")

        if (!d8Dir.exists()) {
            d8Dir.mkdirs()
            extract(context)
        }

        d8Path = "${d8Dir.absolutePath}/d8.jar"
    }

    private fun extract(context: Context) {
        val writer: OutputStream
        val bufSize = 8 * 1024

        val bis = BufferedInputStream(context.assets.open("d8/d8.jar"))
        writer = BufferedOutputStream(FileOutputStream("${d8Dir.absolutePath}/d8.jar"))
        val buf = ByteArray(bufSize)
        var len: Int

        while (bis.read(buf, 0, bufSize).also { len = it } > 0) { writer.write(buf, 0, len) }

        writer.close()
        bis.close()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun runD8(
        arguments: String,
        output: PrintWriter,
        errOutput: PrintWriter,
    ): Int {
        val process = Runtime.getRuntime().exec(
            "dalvikvm -Xmx256m -cp $d8Path com.android.tools.r8.D8 $arguments"
        )

        process.inputStream.redirectTo(output)
        process.errorStream.redirectTo(errOutput)

        process.waitFor()

        return process.exitValue()
    }
}

private fun InputStream.redirectTo(out: PrintWriter) {
    Thread {
        val buffer = ByteArray(1024)
        while (read(buffer) != -1) {
            out.print(String(buffer))
        }
    }.run()
}