package com.blokkok.app.managers.projects

import java.io.File
import kotlin.coroutines.CoroutineContext

class ProjectEditor(
    private val dataDir: File
) {

    private val javaFolder = File(dataDir, "java")
    private val layoutFolder = File(dataDir, "res/layout")
    private val manifestFile = File(dataDir, "AndroidManifest.xml")

    init {
        if (!javaFolder.exists()) javaFolder.mkdir()
        if (!layoutFolder.exists()) layoutFolder.mkdir()
        if (!manifestFile.exists()) manifestFile.createNewFile()
    }

    /**
     * Returns a list of [JavaFile]
     */
    fun listJava(folder: File = javaFolder, currentPackage: String = ""): List<JavaFile> =
        ArrayList<JavaFile>().apply {
            folder.listFiles()!!.forEach {
                if (it.isDirectory) {
                    addAll(listJava(it, "$currentPackage.${it.name}"))
                } else {
                    add(JavaFile(it.nameWithoutExtension, currentPackage))
                }
            }
        }

    /**
     * Returns a list of layout names without it's extension, example: "main"
     */
    fun listLayout(): List<String> = layoutFolder.listFiles()!!.map { it.nameWithoutExtension }

    /**
     * Write code to a java file, will create a new file if the given name doesn't exist
     */
    fun writeJavaCode(packageName: String, name: String, code: String) {
        val packagePath = packageName.replace(".", "/")

        if (!File(javaFolder, packagePath).exists()) File(javaFolder, packagePath).mkdirs()

        val file = File(javaFolder, "$packagePath/$name.java")

        if (!file.exists()) file.createNewFile()

        file.writeText(code)
    }

    /**
     * Read the code of a java file, will return null if the given class doesn't exist
     */
    fun readJavaCode(name: String): String? {
        val path = name.replace(".", "/")
        val file = File(javaFolder, "$path.java")

        return if (!file.exists()) null else file.readText()
    }

    /**
     * Write code to a layout xml file, will create a new file if the given name doesn't exist
     */
    fun writeLayoutCode(name: String, code: String) {
        val file = File(layoutFolder, "$name.xml")

        if (!layoutFolder.exists()) layoutFolder.mkdirs()
        if (!file.exists()) file.createNewFile()

        file.writeText(code)
    }

    /**
     * Read the code of a layout xml file, will return null if the given class doesn't exist
     */
    fun readLayoutCode(name: String): String? {
        val file = File(layoutFolder, "$name.xml")

        return if (!file.exists()) null else file.readText()
    }

    /**
     * Writes the given code into the AndroidManifest.xml file
     */
    fun writeManifest(code: String) = manifestFile.writeText(code)

    /**
     * Returns the content of the AndroidManifest.xml file
     */
    fun readManifest(): String? {
        val content = manifestFile.readText()
        return if (content == "") null else content
    }

    companion object {
        fun generateDefaultManifest(name: String, packageName: String): String =
"""<?xml version="1.0" encoding="utf-8"?>
<manifest 
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="$packageName"
    android:versionCode="1"
    android:versionName="1.0">

    <uses-sdk
        android:minSdkVersion="21"
        android:targetSdkVersion="30" />

    <application
        android:allowBackup="true"
        android:label="$name">
        <activity
            android:name=".MainActivity"
            android:label="$name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>"""
    }
}