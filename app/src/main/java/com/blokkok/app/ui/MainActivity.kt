package com.blokkok.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blokkok.app.R
import com.blokkok.app.databinding.ActivityMainBinding
import com.blokkok.app.ui.fragments.*
import com.blokkok.modsys.ModuleManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, MainFragment())
            .commit()

        ModuleManager.executeCommunications {
            claimFlag(ON_NAV_SELECTED_FLAG)

            createFunction("support_fragment_manager") {
                return@createFunction supportFragmentManager
            }

            createFunction("main_fragment_container_id") {
                return@createFunction R.id.main_fragment_container
            }

            createFunction("drawer_fragment_container_id") {
                return@createFunction R.id.fragmentContainer
            }
        }
    }
}
