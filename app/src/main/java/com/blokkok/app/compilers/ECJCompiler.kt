package com.blokkok.app.compilers

import android.content.Context
import java.io.*

// TODO: 7/16/21 Move android.jar extraction to somewhere else

object ECJCompiler : JavaCompiler {

    private lateinit var compilerDir: File
    private lateinit var ecjPath: String
    private lateinit var androidJarPath: String

    override fun initialize(context: Context) {
        compilerDir = File(context.applicationInfo.dataDir, "binaries/ecj")

        ecjPath = "${compilerDir.absolutePath}/ecj.jar"
        androidJarPath = "${context.applicationInfo.dataDir}/binaries/android.jar"

        if (!compilerDir.exists()) {
            compilerDir.mkdirs()
            extract(context)
        }
    }

    private fun extract(context: Context) {
        for (entry in mapOf("ecj/ecj.jar" to ecjPath, "android.jar" to androidJarPath)) {
            val dexWriter: OutputStream
            val bufSize = 8 * 1024

            val bis = BufferedInputStream(context.assets.open(entry.value))
            dexWriter = BufferedOutputStream(FileOutputStream(entry.key))
            val buf = ByteArray(bufSize)
            var len: Int

            while (bis.read(buf, 0, bufSize).also { len = it } > 0) { dexWriter.write(buf, 0, len) }

            dexWriter.close()
            bis.close()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun compileJava(
        rootPackageFolder: File,
        outputFolder: File,
        stdout: PrintWriter,
        stderr: PrintWriter
    ): Int {
        val process = Runtime.getRuntime().exec(
            "dalvikvm -Xmx256m -Xcompiler-option --compiler-filter=speed -cp $ecjPath org.eclipse.jdt.internal.compiler.batch.Main -proc:none -7 -cp $androidJarPath ${rootPackageFolder.absolutePath} -verbose -d ${outputFolder.absolutePath}"
        )

        process.inputStream.redirectTo(stdout)
        process.errorStream.redirectTo(stderr)

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