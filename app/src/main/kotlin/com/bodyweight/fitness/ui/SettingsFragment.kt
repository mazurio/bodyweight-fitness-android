package com.bodyweight.fitness.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment

import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.R

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.settings)

        updatePreferenceSummaryForKey(Constants.preferencesWeightMeasurementUnitsKey)
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
        if (key.matches(Constants.preferencesWeightMeasurementUnitsKey.toRegex())) {
            val listPreference = findPreference(key) as ListPreference

            listPreference.summary = listPreference.entry
        }
    }
}