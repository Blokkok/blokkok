package com.blokkok.app.processors.compilers

import android.content.Context
import com.blokkok.app.processors.JavaCompiler
import java.io.*

// TODO: 7/16/21 Move android.jar extraction to somewhere else

object ECJCompiler : JavaCompiler {

    override val name: String get() = "ECJ"

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

            val bis = BufferedInputStream(context.assets.open(entry.key))
            dexWriter = BufferedOutputStream(FileOutputStream(entry.value))
            val buf = ByteArray(bufSize)
            var len: Int

            while (bis.read(buf, 0, bufSize).also { len = it } > 0) { dexWriter.write(buf, 0, len) }

            dexWriter.close()
            bis.close()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun compileJava(
        inputFolders: Array<File>,
        outputFolder: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int {
        val process = Runtime.getRuntime().exec(
            "dalvikvm -Xmx256m -Xcompiler-option --compiler-filter=speed -cp $ecjPath org.eclipse.jdt.internal.compiler.batch.Main -proc:none -7 -cp $androidJarPath ${inputFolders.joinToString(" ")} -verbose -d ${outputFolder.absolutePath}"
        )

        process.inputStream.redirectTo(stdout)
        process.errorStream.redirectTo(stderr)

        process.waitFor()

        return process.exitValue()
    }
}

private fun InputStream.redirectTo(out: (String) -> Unit) {
    Thread {
        BufferedReader(InputStreamReader(this)).also { reader ->
            reader.forEachLine { out(it) }
        }.close()
    }.run()
}