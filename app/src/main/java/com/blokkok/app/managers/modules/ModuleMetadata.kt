package com.blokkok.app.managers.modules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModuleMetadata(
    @SerialName("name")     val moduleName: String,
    @SerialName("version")  val moduleVersion: String,
    @SerialName("id")       val moduleId: String,
    @SerialName("jar")      val jarPath: String,
)
