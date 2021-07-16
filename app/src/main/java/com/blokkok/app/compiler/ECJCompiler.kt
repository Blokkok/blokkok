package com.blokkok.app.compiler

import android.content.Context
import android.util.Log
import java.io.*

object ECJCompiler {

    private lateinit var compilerDir: File
    private lateinit var ecjPath: String
    private lateinit var androidJarPath: String

    fun initialize(context: Context) {
        compilerDir = File(context.applicationInfo.dataDir, "binaries/ecj")

        if (!compilerDir.exists()) {
            compilerDir.mkdirs()
            extract(context)
        }

        ecjPath = "${compilerDir.absolutePath}/ecj.jar"
        androidJarPath = "${compilerDir.absolutePath}/android.jar"
    }

    private fun extract(context: Context) {
        for (entry in mapOf("ecj/ecj.jar" to "ecj.jar", "ecj/android.jar" to "android.jar")) {
            val dexWriter: OutputStream
            val bufSize = 8 * 1024

            val bis = BufferedInputStream(context.assets.open(entry.value))
            dexWriter = BufferedOutputStream(FileOutputStream("${compilerDir.absolutePath}/${entry.key}"))
            val buf = ByteArray(bufSize)
            var len: Int

            while (bis.read(buf, 0, bufSize).also { len = it } > 0) { dexWriter.write(buf, 0, len) }

            dexWriter.close()
            bis.close()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun compile(
        directory: String,
        outDirectory: String,
        output: PrintWriter,
        errOutput: PrintWriter,
    ): Int {
        val process = Runtime.getRuntime().exec(
            "dalvikvm -Xmx256m -Xcompiler-option --compiler-filter=speed -cp $ecjPath org.eclipse.jdt.internal.compiler.batch.Main -proc:none -7 -cp $androidJarPath $directory -verbose -d $outDirectory"
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
            Log.d("REDURECT", "redirectTo: ${String(buffer)}")
            out.print(String(buffer))
        }
    }.run()
}