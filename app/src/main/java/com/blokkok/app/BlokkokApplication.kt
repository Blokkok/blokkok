package com.blokkok.app

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.blokkok.modsys.ModuleManager
import com.blokkok.modsys.communication.objects.Broadcaster
import kotlin.system.exitProcess


class BlokkokApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize stuff
        ModuleManager.initialize(this)
        initializeExceptionHandler()

        // Initialize some communications
        ModuleManager.executeCommunications {
            createFunction("getApplicationContext") {
                return@createFunction this@BlokkokApplication
            }

            crashBroadcaster = createBroadcaster("onCrash")
        }
    }

    private lateinit var crashBroadcaster: Broadcaster

    private fun initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            Log.e("BlokkokApplication", "Blokkok crashed", ex)

            crashBroadcaster.broadcast(ex)

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