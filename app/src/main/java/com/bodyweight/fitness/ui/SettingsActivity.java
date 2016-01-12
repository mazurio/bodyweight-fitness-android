package com.bodyweight.fitness.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBar();

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setTitle(R.string.action_settings);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);

            updatePreferenceSummaryForKey(Constants.PREFERENCE_WEIGHT_MEASUREMENT_UNITS);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreferenceSummaryForKey(key);
        }

        public void updatePreferenceSummaryForKey(String key) {
            if (key.matches(Constants.PREFERENCE_WEIGHT_MEASUREMENT_UNITS)) {
                ListPreference listPreference = (ListPreference) findPreference(key);
                listPreference.setSummary(listPreference.getEntry());
            }
        }
    }
}
