package com.blokkok.app

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.blokkok.app.managers.CommonFilesManager
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.managers.projects.ProjectsManager
import com.blokkok.app.processors.compilers.ECJCompiler
import com.blokkok.app.processors.dexers.D8Dexer
import com.blokkok.app.processors.signers.AndroidApkSigner
import com.blokkok.modsys.ModuleManager
import kotlin.system.exitProcess


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

        initializeExceptionHandler()
    }

    private fun initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            Log.e("BlokkokApplication", "Blokkok crashed", ex)

            val intent = Intent(applicationContext, DebugActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("error", ex.stackTraceToString())
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am[AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000] = pendingIntent

            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }
}