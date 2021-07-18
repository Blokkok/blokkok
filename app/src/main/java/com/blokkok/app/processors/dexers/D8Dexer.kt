package com.blokkok.app.processors.dexers

import android.content.Context
import com.blokkok.app.processors.Dexer
import java.io.*

object D8Dexer : Dexer {

    override val name: String get() = "D8"

    private lateinit var d8Dir: File
    private lateinit var d8Path: String
    private lateinit var androidJarPath: String

    override fun initialize(context: Context) {
        d8Dir = File(context.applicationInfo.dataDir, "binaries/d8")
        androidJarPath = "${context.applicationInfo.dataDir}/binaries/android.jar"

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
    override suspend fun dex(
        rootPackageFolder: File,
        output: File,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
    ): Int {
        val process = Runtime.getRuntime().exec(
            "dalvikvm -Xmx256m -cp $d8Path com.android.tools.r8.D8 --release --classpath $androidJarPath --output ${output.absolutePath} ${listAllFiles(rootPackageFolder).joinToString(" ")}"
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

private fun listAllFiles(folder: File): List<String> {
    if (!folder.exists() || !folder.isDirectory) return emptyList()

    return ArrayList<String>().apply {
        folder.listFiles()!!.forEach { file ->
            if (file.isDirectory) {
                addAll(listAllFiles(file))
            } else {
                add(file.absolutePath)
            }
        }
    }
}