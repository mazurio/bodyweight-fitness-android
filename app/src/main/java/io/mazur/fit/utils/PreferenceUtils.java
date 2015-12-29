package io.mazur.fit.utils;

import android.preference.PreferenceManager;

import io.mazur.fit.App;
import io.mazur.fit.Constants;
import io.mazur.fit.R;
import io.mazur.fit.model.WeightMeasurementUnit;

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
        PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit()
                .putLong(Constants.PREFERENCE_TIMER_KEY, value).commit();
    }

    public long getTimerValue(long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getLong(Constants.PREFERENCE_TIMER_KEY, defaultValue);
    }
}
