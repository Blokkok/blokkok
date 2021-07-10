package com.blokkok.app.managers.modules

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object ModuleManager {

    private lateinit var dataDir: File
    private lateinit var modulesDir: File

    fun initialize(context: Context) {
        dataDir = File(context.applicationInfo.dataDir)
        modulesDir = File(context.applicationInfo.dataDir, "modules")

        // Initialize the modules dir if it doesn't exists
        if (!modulesDir.exists()) modulesDir.mkdir()
    }

    fun getModules(): List<ModuleMetadata> =
        modulesDir.listFiles()!!.map { file ->
            val meta = File(file, "meta.json").readText()
            return@map Json.decodeFromString(meta)
        }

    fun getModule(id: String): ModuleMetadata =
        Json.decodeFromString(
            File(modulesDir, id).readText()
        )

    // TODO: 7/10/21 Add import modules
}