package com.blokkok.app.managers

import android.content.Context
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

// Use the default extraction to home folder + execute it there
const val useLegacyMethod = true

// Change this when you changed the binaries.zip
const val binariesABI = "armeabi-v7a"

// TODO: 7/10/21 Make a test for this class

object NativeBinariesManager {

    private lateinit var nativeLibraryDir: File
    private lateinit var dataDir: File
    private lateinit var binariesDir: File

    /**
     * Used to indicate whether do we need to extract the binaries or not.
     * Call NativeBinariesManager#executeBinaries(Context) if this value is true
     */

    fun initialize(context: Context) {
        nativeLibraryDir = File(context.applicationInfo.nativeLibraryDir)
        dataDir = File(context.applicationInfo.dataDir)
        binariesDir = File(dataDir, "binaries")

        if (useLegacyMethod) {
            // Check if the binaries are already extracted
            if (!binariesDir.exists()) {
                // Does not seem so, then extract the binaries
                binariesDir.mkdir()
                extractBinaries(context)
            }

        } else {
            throw NotImplementedError("Non-legacy binary execution method is not implemented yet")
        }
    }

    private fun extractBinaries(context: Context) {
        Thread {
            /* The structure of the binaries.zip would be
             *
             * aapt2
             * L bin
             * | L aapt2
             * L lib
             *   L (aapt2's libraries)
             *
             * zipalign
             * L bin
             * | L zipalign
             */
            unpackZip(ZipInputStream(context.assets.open("binaries.zip")), binariesDir)

            // Then make them executable
            NativeBinaries.values().forEach {
                File(getBinaryPath(it)).setExecutable(true)
            }
        }.run()
    }

    fun executeCommand(
        binary: NativeBinaries,
        arguments: List<String>,
        outputStream: OutputStream
    ) {
        val process = Runtime.getRuntime().exec(
            ArrayList<String>().apply {
                add(getBinaryPath(binary))
                addAll(arguments)
            }.toTypedArray())

        process.inputStream.redirectStreamTo(outputStream)
    }

    private fun getBinaryPath(binary: NativeBinaries): String {
        if (useLegacyMethod) {
            return when (binary) {
                NativeBinaries.AAPT2 -> File(binariesDir, "aapt2/bin/aapt2").absolutePath
                NativeBinaries.ZIP_ALIGN -> File(binariesDir, "zipalign/bin/zipalign").absolutePath
            }
        } else {
            throw NotImplementedError("Non-legacy binary execution method is not implemented yet")
        }
    }

    enum class NativeBinaries {
        AAPT2,
        ZIP_ALIGN,
    }
}

// Used to redirect InputStreams to an OutputStream
private fun InputStream.redirectStreamTo(dest: OutputStream) {
    Thread {
        val buffer = ByteArray(1024)
        var length: Int

        while (read(buffer).also { length = it } != -1)
            dest.write(buffer, 0, length)
    }.start()
}

// Used to unpack zip files into a specified output path
private fun unpackZip(zipInputStream: ZipInputStream, outputPath: File): Boolean {
    try {
        var filename: String

        var entry: ZipEntry?
        val buffer = ByteArray(1024)
        var count: Int

        while (zipInputStream.nextEntry.also { entry = it } != null) {
            filename = entry!!.name

            if (entry!!.isDirectory) {
                File(outputPath, filename).mkdirs()
                continue
            }

            val fileOut = FileOutputStream(File(outputPath, filename))

            while (zipInputStream.read(buffer).also { count = it } != -1)
                fileOut.write(buffer, 0, count)

            fileOut.close()
            zipInputStream.closeEntry()
        }

        zipInputStream.close()

    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }

    return true
}