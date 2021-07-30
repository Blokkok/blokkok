package com.blokkok.app

import android.app.Application
import com.blokkok.app.managers.CommonFilesManager
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.managers.projects.ProjectsManager
import com.blokkok.app.processors.compilers.ECJCompiler
import com.blokkok.app.processors.dexers.D8Dexer
import com.blokkok.app.processors.signers.AndroidApkSigner
import com.blokkok.modsys.ModuleManager

class BlokkokApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize stuff
        ProjectsManager         .initialize(this)
        NativeBinariesManager   .initialize(this)
        ModuleManager           .initialize(this)
        ECJCompiler             .initialize(this)
        D8Dexer                 .initialize(this)
        AndroidApkSigner        .initialize(this)
        LibraryManager          .initialize(this)
        CommonFilesManager      .initialize(this)
    }
}