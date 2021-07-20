package com.blokkok.app.managers.libraries

import android.content.Context
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.processors.ProcessorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Pattern
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
 *   | L dex
 *   | | L classes1.dex
 *   | | L classes2.dex
 *   | | L ...
 *   | L classes.jar            -- the bytecode jar
 *   | L res.zip                -- compiled resources
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

    fun listCachedLibraries(): List<CachedLibrary> =
        cacheDir.listFiles()!!.mapNotNull {
            if (!it.resolve("meta.json").exists()) return@mapNotNull null
            Json.decodeFromString(it.resolve("meta.json").readText())
        }

    fun getCachedLibrary(name: String): CachedLibrary =
        Json.decodeFromString(cacheDir.resolve(name).resolve("meta.json").readText())

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun compileLibrary(
        name: String,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit,
    ): Int {
        val aarFile = aarsDir.resolve("$name.aar")
        if (!aarFile.exists()) return 100 // check if the aar file given actually exists
                    // 100 is a unique number used to identify that this library doesn't exist

        val aarCacheDir        = cacheDir.resolve(name)
        val dexOutputDir       = cacheDir.resolve(name).resolve("dex")
        val resourcesZipOutput = cacheDir.resolve(name).resolve("res.zip")
        val bytecodeClassesJar = cacheDir.resolve(name).resolve("classes.jar")

        var packageName: String

        // Clear the cache first before compiling it again
        if (aarCacheDir.exists()) clearCache(name)

        // Don't forget to create the folders
        aarCacheDir.mkdirs()
        dexOutputDir.mkdir()

        val retVal = withContext(Dispatchers.IO) {
            // First, we're going to need to extract the classes jar (and the res folder) and dex it
            unpackAar(ZipInputStream(FileInputStream(aarFile)), aarCacheDir)

            // Read the package name from AndroidManifest.xml
            val androidManifest = aarCacheDir.resolve("AndroidManifest.xml").readText()
            val matcher = Pattern.compile("package=\"(.*)\"").matcher(androidManifest)

            if (matcher.find()) {
                packageName = matcher.group(1)!!
            } else {
                // this is weird, AndroidManifest.xml doesn't contain the package name
                stderr("This library's AndroidManifest.xml doesn't contain the package name for some reason")
                return@withContext 1
            }

            // Dex the classes_bytecode.jar with the dexer
            val dexer = ProcessorPicker.pickDexer()

            stdout("${dexer.name} is starting to dex the library")

            val dexerRetVal = dexer.dex(bytecodeClassesJar, dexOutputDir,
                    { stdout("${dexer.name} >> $it") },
                    { stderr("${dexer.name} ERR >> $it") }
                )

            if (dexerRetVal != 0) {
                stderr("${dexer.name} returned a non-zero status (something bad happened)")
                return@withContext dexerRetVal
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
                stderr("AAPT2 returned a non-zero status (something bad happened)")
                return@withContext aapt2RetVal
            } else {
                stdout("AAPT2 has finished compiling resources")
            }

            // and finally, create the meta.json file
            File(aarCacheDir, "meta.json").writeText(
                Json.encodeToString(
                    CachedLibrary(name, packageName, aarFile.absolutePath, aarCacheDir.absolutePath)
                )
            )

            return@withContext 0
        }

        // Clean the res folder and the AndroidManifest.xml file because they aren't needed anymore
        aarCacheDir.resolve("res").deleteRecursively()
        aarCacheDir.resolve("AndroidManifest.xml").delete()

        return retVal
    }

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

            // Only extract classes.jar, the res/ folder and AndroidManifest.xml
            if (filename != "classes.jar") {
                if (!filename.startsWith("res"))
                    if (filename != "AndroidManifest.xml")
                        continue
            }

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