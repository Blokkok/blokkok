package com.blokkok.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentMainBinding
import com.blokkok.app.ui.fragments.*
import com.blokkok.modsys.ModuleManager
import com.google.android.material.navigation.NavigationView
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerNavView: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolBar

        drawerLayout = binding.drawerLayout
        drawerNavView = binding.navView

        drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        drawerNavView.setNavigationItemSelectedListener(::onNavigationItemSelected)

        if (savedInstanceState == null) {
            toolbar.subtitle = ""
            val fragmentTransaction = parentFragmentManager.beginTransaction()

            fragmentTransaction
                .replace(R.id.fragmentContainer, HomeFragment(openModuleManagerCallback = {
                    drawerNavView.menu.performIdentifierAction(R.id.modules, 0)
                }))
                .commit()
        }

        ModuleManager.executeCommunications {
            createFunction("main_drawer_menu")            { drawerNavView.menu }
            createFunction("drawer_menu_main_group_id")   { R.id.drawer_menu_main_group }
            createFunction("drawer_menu_social_group_id") { R.id.drawer_menu_social_group }
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent()
        val drawerFragmentTransaction = parentFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.home_item -> {
                drawerNavView.setCheckedItem(R.id.home_item)

                toolbar.subtitle = ""

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

                toolbar.subtitle = "Modules"

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

            R.id.about -> {
                drawerNavView.setCheckedItem(R.id.about)

                toolbar.subtitle = "About"

                val aboutFragment = AboutFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, aboutFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            R.id.license -> {
                drawerNavView.setCheckedItem(R.id.license)

                toolbar.subtitle = "License"

                val licensesFragment = LicenseFragment()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, licensesFragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)

                return true
            }

            R.id.settings -> {
                drawerNavView.setCheckedItem(R.id.settings)

                toolbar.subtitle = "Settings"

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

            // check other modules that have implemented onNavigationItemSelected
            else -> {
                var processed = false

                ModuleManager.executeCommunications {
                    // Check every modules that has this flag
                    val namespaces = getFlagNamespaces(ON_NAV_SELECTED_FLAG)

                    for (namespace in namespaces) {
                        // Check if they have the function
                        if (getCommunication(namespace, "onNavigationItemSelected") == null)
                            continue

                        // Invoke it
                        val args = mapOf(
                            "menu_item" to item
                        )

                        val res = invokeFunction(namespace, "onNavigationItemSelected", args)
                        if (res !is Boolean) continue

                        // Check if it returns true (it processed something)
                        if (res) {
                            processed = true
                            return@executeCommunications
                        }
                    }
                }

                return processed
            }
        }
    }
}
