package com.blokkok.app

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blokkok.app.databinding.ActivityDebugBinding

class DebugActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugBinding

    // Little easter egg :P
    private val easterEggHeaders = arrayOf(
        "The app crashed, what did you do?",
        "Looks like the app has crashed :(",
        "The app crashed, are you okay mate",
        "KABOOM! App just crashed",
        "App magically crashed, great",
        "aight app crash, time to fix bugs",
        "You're so sus, the app crashed few seconds ago",
        "App crashed, Iyxan23 must've put too much hels emojis",
        "TheClashFruit was going nuts and changed something then the app crashed",
        "The app crashed, ryenyuku was probably trying to convert the source code into java",
        "Blokkok crashed. Android studio, you have ONE JOB",
        "Hackers are mining bitcoin on your device. Just kidding, the app crashed",
        "Crashed and Furious - Blokkok edition",
        "The Legend of Zelda: A Link to the Crash & Four Swords",
        "Hmm, the app crashed, it might be the man in the chicken costume",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reportButton = binding.reportButton
        val errorTextView = binding.errorTextView
        val header = binding.debugHeader

        // 1/5 chance you will get an easter egg, good luck :P
        header.text = if ((1..5).random() == 1) easterEggHeaders.random()
                      else "The app crashed"

        errorTextView.text = intent.getStringExtra("error")

        reportButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Soon...")
                .setMessage("Currently you cannot report errors.")
                .setPositiveButton("Ok") { _, _ ->
                    finishAffinity();
                }
                .show();
        }
    }
}