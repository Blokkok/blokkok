package com.blokkok.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blokkok.app.fragments.main.AboutFragment
import com.blokkok.app.fragments.main.HomeFragment
import com.blokkok.app.fragments.main.ModulesFragment
import com.blokkok.app.fragments.main.SettingsFragment
import com.blokkok.app.managers.NativeBinariesManager
import com.blokkok.app.managers.modules.ModuleManager
import com.blokkok.app.managers.projects.ProjectsManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerNavView: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeManagers()

        val actionBar = findViewById<View>(R.id.toolBar) as Toolbar
        setSupportActionBar(actionBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        drawerNavView = findViewById(R.id.nav_view)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            actionBar,
            R.string.app_name,
            R.string.app_name
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        drawerNavView.setNavigationItemSelectedListener(this)

        /*
        val sharedPreferences = getPreferences(MODE_PRIVATE)

        if(sharedPreferences.getBoolean("dark_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        */

        if (savedInstanceState == null) {
            supportActionBar!!.subtitle = "Projects"
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            fragmentTransaction
                .replace(R.id.fragmentContainer, HomeFragment.newInstance())
                .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent()
        val drawerFragmentTransaction = supportFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.projects -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                drawerNavView.setCheckedItem(R.id.projects)

                supportActionBar!!.subtitle = "Projects"

                val homeFragment = HomeFragment.newInstance()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, homeFragment)
                    .commit()

                return true
            }

            R.id.modules -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                drawerNavView.setCheckedItem(R.id.modules)

                supportActionBar!!.subtitle = "Modules"

                val modulesFragment = ModulesFragment.newInstance()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, modulesFragment)
                    .commit()

                return true
            }

            R.id.about -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                drawerNavView.setCheckedItem(R.id.about)

                supportActionBar!!.subtitle = "About"

                val aboutFragment = AboutFragment.newInstance()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, aboutFragment)
                    .commit()

                return true
            }

            R.id.settings -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                drawerNavView.setCheckedItem(R.id.settings)

                supportActionBar!!.subtitle = "Settings"

                val settingsFragment = SettingsFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, settingsFragment)
                    .commit()

                return true
            }

            R.id.dc -> {
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://discord.gg/")
                startActivity(intent)

                return true
            }

            R.id.gh -> {
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://github.com/Blokkok")
                startActivity(intent)

                return true
            }

            R.id.web -> {
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://blokkok.tk/")
                startActivity(intent)

                return true
            }
        }

        return false
    }

    private fun initializeManagers() {
        ProjectsManager.initialize(this)
        NativeBinariesManager.initialize(this)
        ModuleManager.initialize(this)
    }
}