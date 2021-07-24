package com.blokkok.app.managers.libraries

import android.content.Context
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.managers.unpackZip
import com.blokkok.app.processors.ProcessorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.io.*
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/* Structure of the libraries folder
 *
 * libraries
 * L libraries.json             -- the file that contains info about libraries in here
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

// TODO: 7/21/21 Use library name as a key to get it's data in libraries.json

object LibraryManager {

    private lateinit var dataDir: File
    private lateinit var librariesDir: File
    private lateinit var librariesMeta: File
    private lateinit var aarsDir: File
    private lateinit var cacheDir: File

    fun initialize(context: Context) {
        dataDir = File(context.applicationInfo.dataDir)
        librariesDir = File(dataDir, "libraries")
        librariesMeta = File(librariesDir, "libraries.json")
        aarsDir = File(librariesDir, "aars")
        cacheDir = File(librariesDir, "cache")

        // Mkdir the directories if it doesn't exist
        if (!librariesDir.exists()) {
            aarsDir.mkdirs()
            cacheDir.mkdirs()

            librariesMeta.writeText("{}")
        }
    }

    // I'm using both JSONObject and kotlinx.serialization since kotlinx.serialization can't list elements
    // this is going to have a quite performance hit, I'll update them when I find a better solution
    fun listLibraries(): List<Library> =
        JSONObject(librariesMeta.readText()).let { json ->
            return json.keys().asSequence().toList().map {
                Json.decodeFromString(json.getJSONObject(it).toString())
            }
        }

    private fun putLibraryEntry(name: String, library: Library) {
        val librariesMetaJson = JSONObject(librariesMeta.readText())
        librariesMetaJson.put(name, JSONObject(Json.encodeToString(library)))
        librariesMeta.writeText(librariesMetaJson.toString())
    }

    private fun deleteLibraryEntry(name: String) {
        val librariesMetaJson = JSONObject(librariesMeta.readText())
        if (!librariesMetaJson.has(name)) return
        librariesMetaJson.remove(name)
        librariesMeta.writeText(librariesMetaJson.toString())
    }

    fun getLibraryEntry(name: String): Library? {
        val librariesMetaJson = JSONObject(librariesMeta.readText())
        if (!librariesMetaJson.has(name)) return null

        return Json.decodeFromString(librariesMetaJson.getJSONObject(name).toString())
    }

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

            // Dex the classes.jar with the dexer
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

            // and finally, change this library type to be CACHED and also add it's cache folder
            putLibraryEntry(
                name,
                Library(
                    name,
                    LibraryType.CACHED,
                    packageName,
                    getLibraryEntry(name)!!.aarPath,
                    cacheDir.relativeTo(librariesMeta).absolutePath
                )
            )

            return@withContext 0
        }

        // Clean the res folder and the AndroidManifest.xml file because they aren't needed anymore
        aarCacheDir.resolve("res").deleteRecursively()
        aarCacheDir.resolve("AndroidManifest.xml").delete()

        return retVal
    }

    fun addAARLibrary(stream: InputStream, name: String) {
        val nameWithoutExtension = aarsDir.resolve(name).nameWithoutExtension
        // pretty straightforward
        aarsDir.resolve(name).writeBytes(stream.readBytes())

        // then add a new library entry
        putLibraryEntry(nameWithoutExtension,
            Library(
                nameWithoutExtension,
                LibraryType.NOT_CACHED,
                aarPath = aarsDir.resolve(name).absolutePath
            )
        )
    }

    fun addPrecompiledLibrary(zipFile: InputStream) {
        /* The structure of the precompiled library zip is:
         *
         * file.zip
         * L name
         * L package
         * L classes.jar
         * L dex
         * | L classes.dex
         * | L ...
         * L res.zip
         *
         * or basically the structure of the cache
         */
        // Create a temporary folder
        val temp = File(dataDir, "cache/temp")
        temp.mkdirs()

        // then unpack the zip on that temporary folder
        unpackZip(ZipInputStream(zipFile), temp)
        val name = temp.resolve("name").readText() // read the name
        val packageName = temp.resolve("package").readText() // and the package name

        // then delete those files
        temp.resolve("name").delete()
        temp.resolve("package").delete()
        temp.renameTo(cacheDir.resolve(name)) // move these files to the cache directory

        // then delete it
        temp.delete()

        // finally, add a new library entry
        putLibraryEntry(
            name,
            Library(
                name,
                LibraryType.PRECOMPILED,
                packageName,
                cacheFolderPath = librariesDir.resolve(name).absolutePath
            )
        )
    }

    fun clearCache(name: String) {
        cacheDir.resolve(name).deleteRecursively()

        // set the library entry to be NOT_CACHED
        putLibraryEntry(name, getLibraryEntry(name)!!.copy(type = LibraryType.NOT_CACHED))
    }

    fun deleteLibrary(name: String) {
        val library = getLibraryEntry(name)!!

        // Delete the files depending on their type
        when (library.type) {
            LibraryType.NOT_CACHED -> File(library.aarPath!!).delete()
            LibraryType.PRECOMPILED -> File(library.cacheFolderPath!!).deleteRecursively()
            LibraryType.CACHED -> {
                File(library.aarPath!!).delete()
                File(library.cacheFolderPath!!).deleteRecursively()
            }
        }

        // and delete the entry
        deleteLibraryEntry(name)
    }
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