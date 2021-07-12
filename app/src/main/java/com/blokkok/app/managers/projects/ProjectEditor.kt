package com.blokkok.app.managers.projects

import java.io.File

class ProjectEditor(
    private val dataDir: File
) {

    private val javaFolder = File(dataDir, "java")
    private val layoutFolder = File(dataDir, "res/layout")

    init {
        if (!javaFolder.exists()) javaFolder.mkdir()
        if (!layoutFolder.exists()) layoutFolder.mkdir()
    }

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
     * Used to read and write java files
     * Example(s):
     * val mainActivityCode = java["com.hello.world.MainActivity"]
     * java["com.hello.world.MainActivity"] = "// oops, it's gone now"
     */
    val java = object {
        /**
         * Read the code of a java file, will return null if the given class doesn't exist
         */
        fun get(name: String): String? {
            val path = name.replace(".", "/")
            val file = File(javaFolder, "$path.java")

            return if (!file.exists()) null else file.readText()
        }

        /**
         * Write code to a java file, will create a new file if the given name doesn't exist
         */
        fun set(name: String, code: String) {
            val path = name.replace(".", "/")
            val file = File(javaFolder, "$path.java")

            if (!file.exists()) file.createNewFile()

            file.writeText(code)
        }
    }

    val layout = object {
        /**
         * Read the code of a layout xml file, will return null if the given class doesn't exist
         */
        fun get(name: String): String? {
            val file = File(layoutFolder, "$name.xml")

            return if (!file.exists()) null else file.readText()
        }

        /**
         * Write code to a layout xml file, will create a new file if the given name doesn't exist
         */
        fun set(name: String, code: String) {
            val file = File(javaFolder, "$name.xml")
            if (!file.exists()) file.createNewFile()

            file.writeText(code)
        }
    }
}