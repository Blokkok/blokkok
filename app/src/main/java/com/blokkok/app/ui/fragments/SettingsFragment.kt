package com.blokkok.app.ui.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blokkok.app.R
import com.blokkok.modsys.namespace.NamespaceResolver

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.run {
            AppCompatDelegate.setDefaultNightMode(
                if (getBoolean(key, false)) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return preference?.let { pref ->
            when (pref.key) {
                "dump_global_namespace" -> {
                    Log.d("NamespaceDump", "Global namespace dump:\n" + StringBuilder().apply {
                        NamespaceResolver.globalNamespace.prettyPrint {
                            appendLine(it)
                        }
                    }.toString())

                    true
                }

                else -> false
            }
        } ?: false
    }
}