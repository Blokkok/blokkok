package com.blokkok.app.managers.libraries

import android.content.Context
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.processors.ProcessorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/* Structure of the libraries folder
 *
 * libraries
 * L aars                       -- this will be the directory where the aars are stored
 * | L appcompat-1.3.0.aar
 * | L etc..
 * L cache                      -- this will be the cache directory where the compiled dex file and the resources are stored
 *   L appcompat-1.3.0           | compiling the library to a cache will be decided by the user, or when compiling an apk
 *   | L classes.dex
 *   | L res.zip
 *   L etc..
 */

object LibraryManager {

    private lateinit var dataDir: File
    private lateinit var librariesDir: File
    private lateinit var aarsDir: File
    private lateinit var cacheDir: File

    fun initialize(context: Context) {
        dataDir = File(context.applicationInfo.dataDir)
        librariesDir = File(dataDir, "libraries")
        aarsDir = File(librariesDir, "aars")
        cacheDir = File(librariesDir, "cache")

        // Mkdir the directories if it doesn't exist
        if (!librariesDir.exists()) {
            aarsDir.mkdirs()
            cacheDir.mkdirs()

            // and also extract the necessary libraries used for android building
            extract(context)
        }
    }

    private fun extract(context: Context) {
        Thread {
            /* This zip just contains .aar files used to compile the app, those are:
             * androidx.core:core, androidx.appcompat:appcompat, and
             * com.google.android.material:material
             */
            unpackZip(ZipInputStream(context.assets.open("libraries.zip")), aarsDir)
        }.run()
    }

    fun listLibraries(): List<String> = aarsDir.listFiles()!!.map { it.nameWithoutExtension }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun compileLibrary(
        name: String,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
    ): Int {
        val aarFile = aarsDir.resolve("$name.aar")
        if (!aarFile.exists()) return 100 // check if the aar file given actually exists
                    // 100 is a unique number used to identify that this library doesn't exist

        val aarCacheDir = cacheDir.resolve(name)
        val resourcesZipOutput = cacheDir.resolve(name).resolve("res.zip")

        // Clear the cache first before compiling it again
        if (aarCacheDir.exists()) clearCache(name)

        // Don't forget to create the folder
        aarCacheDir.mkdirs()

        val retVal = withContext(Dispatchers.IO) {
            // First, we're going to need to extract the classes jar (and the res folder) and dex it
            unpackAar(ZipInputStream(FileInputStream(aarFile)), aarCacheDir)

            // Rename classes.jar to not conflict with the dexed jar file
            val rawClassesJar = aarCacheDir.resolve("classes.jar")
            rawClassesJar.renameTo(aarCacheDir.resolve("classes_bytecode.jar"))
            // rawClassesJar.createNewFile() // also d8 needs the jar to be created

            // Dex the classes_bytecode.jar with the dexer
            val dexer = ProcessorPicker.pickDexer()

            stdout("${dexer.name} is starting to dex the library")

            val dexerRetVal = dexer.dex(
                    aarCacheDir.resolve("classes_bytecode.jar"),
                    aarCacheDir.resolve("classes.jar"),
                    { stdout("${dexer.name} >> $it") },
                    { stderr("${dexer.name} ERR >> $it") }
                )

            if (dexerRetVal != 0) {
                stderr("${dexer.name} returned a non-zero status (something bad happened)"); return@withContext dexerRetVal
            } else {
                stdout("${dexer.name} has finished dex-ing")
            }

            // And then compile the resources with aapt2
            stdout("\nAAPT2 is starting to compile the resources of the library")

            val aapt2RetVal = NativeBinariesManager.executeCommand(
                NativeBinariesManager.NativeBinaries.AAPT2,
                arrayOf(
                    "compile",
                    "--dir", aarCacheDir.resolve("res/").absolutePath,
                    "-o", resourcesZipOutput.absolutePath,
                    "-v"
                ),
                { stdout("AAPT2 >> $it") },
                { stderr("AAPT2 ERR >> $it") }
            )

            if (aapt2RetVal != 0) {
                stderr("AAPT2 returned a non-zero status (something bad happened)"); return@withContext aapt2RetVal
            } else {
                stdout("AAPT2 has finished compiling resources")
            }

            return@withContext 0
        }

        // Clean the res folder because it's not needed anymore
        aarCacheDir.resolve("res").deleteRecursively()

        return retVal
    }

    fun getClassesDex(name: String): File =
        cacheDir.resolve(name).resolve("classes.jar")

    fun getClassesBytecode(name: String): File =
        cacheDir.resolve(name).resolve("classes_bytecode.jar")

    fun getResourcesZip(name: String): File =
        cacheDir.resolve(name).resolve("res.zip")

    fun isCached(name: String) = cacheDir.resolve(name).exists()
    fun clearCache(name: String) = cacheDir.resolve(name).deleteRecursively()
}

// Used to unpack zip files into a specified output path
private fun unpackZip(
    zipInputStream: ZipInputStream,
    outputPath: File,
): Boolean {
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

// Unpacks the classes.jar and res/ folder
private fun unpackAar(
    zipInputStream: ZipInputStream,
    outputPath: File,
): Boolean {
    try {
        var filename: String

        var entry: ZipEntry?
        val buffer = ByteArray(1024)
        var count: Int

        while (zipInputStream.nextEntry.also { entry = it } != null) {
            filename = entry!!.name

            // Only extract classes.jar and the res/ folder
            if (filename != "classes.jar" || !filename.startsWith("res"))
                continue

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