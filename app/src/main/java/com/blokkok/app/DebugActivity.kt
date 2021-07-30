package com.blokkok.app

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blokkok.app.databinding.ActivityDebugBinding

class DebugActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reportButton = binding.reportButton
        val errorTextView = binding.errorTextView

        errorTextView.text = intent.getStringExtra("error");

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