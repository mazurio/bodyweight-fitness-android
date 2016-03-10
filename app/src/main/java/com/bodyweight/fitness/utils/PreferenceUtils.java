package com.bodyweight.fitness.utils;

import android.preference.PreferenceManager;

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.model.WeightMeasurementUnit;

import com.bodyweight.fitness.R;

public class PreferenceUtils {
    private static class InstanceHolder {
        private static final PreferenceUtils mInstance = new PreferenceUtils();
    }

    public static PreferenceUtils getInstance() {
        return InstanceHolder.mInstance;
    }

    private PreferenceUtils() {
        PreferenceManager.setDefaultValues(App.getContext(), R.xml.settings, false);
    }

    public void setDefaultRoutine(String defaultRoutine) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .edit()
                .putString(Constants.PREFERENCE_DEFAULT_ROUTINE, defaultRoutine)
                .commit();
    }

    public String getDefaultRoutine() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getString(Constants.PREFERENCE_DEFAULT_ROUTINE, "routine0");
    }

    public WeightMeasurementUnit getWeightMeasurementUnit() {
        String value = PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getString(Constants.PREFERENCE_WEIGHT_MEASUREMENT_UNITS, "kg");

        return WeightMeasurementUnit.get(value);
    }

    public boolean playSoundWhenTimerStops() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getBoolean(Constants.PREFERENCE_PLAY_SOUND_WHEN_TIMER_STOPS, true);
    }

    public boolean keepScreenOnWhenAppIsRunning() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getBoolean(Constants.PREFERENCE_KEEP_SCREEN_ON, true);
    }

    public void setTimerValue(long value) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .edit()
                .putLong(Constants.PREFERENCE_TIMER_KEY, value)
                .commit();
    }

    public long getTimerValue(long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getLong(Constants.PREFERENCE_TIMER_KEY, defaultValue);
    }
}
