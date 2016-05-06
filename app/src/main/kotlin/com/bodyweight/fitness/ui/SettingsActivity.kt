package com.bodyweight.fitness.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActionBar()

        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setActionBar() {
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setTitle(R.string.action_settings)
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.settings)

            updatePreferenceSummaryForKey(Constants.PREFERENCE_WEIGHT_MEASUREMENT_UNITS)
        }

        override fun onResume() {
            super.onResume()

            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()

            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            updatePreferenceSummaryForKey(key)
        }

        fun updatePreferenceSummaryForKey(key: String) {
            if (key.matches(Constants.PREFERENCE_WEIGHT_MEASUREMENT_UNITS.toRegex())) {
                val listPreference = findPreference(key) as ListPreference

                listPreference.summary = listPreference.entry
            }
        }
    }
}
