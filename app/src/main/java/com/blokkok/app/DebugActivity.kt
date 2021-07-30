package com.blokkok.app

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        val reportButton = findViewById<Button>(R.id.reportButton);
        val errorTextView = findViewById<TextView>(R.id.errorTextView);

        errorTextView.text = this.intent.getStringExtra("error");

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