package io.mazur.fit.utils;

import android.preference.PreferenceManager;

import io.mazur.fit.App;
import io.mazur.fit.Constants;
import io.mazur.fit.R;

public class PreferenceUtil {
    private static class InstanceHolder {
        private static final PreferenceUtil mInstance = new PreferenceUtil();
    }

    public static PreferenceUtil getInstance() {
        return InstanceHolder.mInstance;
    }

    private PreferenceUtil() {
        PreferenceManager.setDefaultValues(App.getContext(), R.xml.settings, false);
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
