package com.blokkok.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.blokkok.app.R
import com.blokkok.app.databinding.ActivityMainBinding
import com.blokkok.app.ui.fragments.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerNavView: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = binding.toolBar
        setSupportActionBar(actionBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        drawerLayout = binding.drawerLayout
        drawerNavView = binding.navView

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
                .replace(R.id.fragmentContainer, HomeFragment(openModuleManagerCallback =  {
                    drawerNavView.menu.performIdentifierAction(R.id.modules, 0)
                }))
                .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent()
        val drawerFragmentTransaction = supportFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.home_item -> {
                drawerNavView.setCheckedItem(R.id.home_item)

                supportActionBar!!.subtitle = "Projects"

                val homeFragment = HomeFragment(openModuleManagerCallback =  {
                    drawerNavView.menu.performIdentifierAction(R.id.modules, 0)
                })

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, homeFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            R.id.modules -> {
                drawerNavView.setCheckedItem(R.id.modules)

                supportActionBar!!.subtitle = "Modules"

                val modulesFragment = ModulesFragment.newInstance()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, modulesFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            /* R.id.libraries -> {
                drawerNavView.setCheckedItem(R.id.modules)

                supportActionBar!!.subtitle = "Libraries"

                val modulesFragment = LibrariesFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, modulesFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            } */

            R.id.store -> {
                drawerNavView.setCheckedItem(R.id.store);

                supportActionBar!!.subtitle = "Store";

                val storeFragment = StoreFragment();

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, storeFragment)
                    .commit();

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }

            R.id.about -> {
                drawerNavView.setCheckedItem(R.id.about)

                supportActionBar!!.subtitle = "About"

                val aboutFragment = AboutFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, aboutFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            R.id.license -> {
                drawerNavView.setCheckedItem(R.id.license)

                supportActionBar!!.subtitle = "License"

                val licensesFragment = LicenseFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, licensesFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            R.id.settings -> {
                drawerNavView.setCheckedItem(R.id.settings)

                supportActionBar!!.subtitle = "Settings"

                val settingsFragment = SettingsFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, settingsFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

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
                intent.data = Uri.parse("https://blokkok.ga/")
                startActivity(intent)

                return true
            }
        }

        return false
    }
}
