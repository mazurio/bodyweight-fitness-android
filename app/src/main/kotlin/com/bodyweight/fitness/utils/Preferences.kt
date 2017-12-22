package com.bodyweight.fitness.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.bodyweight.fitness.App
import com.bodyweight.fitness.Constants

import com.bodyweight.fitness.R
import com.bodyweight.fitness.model.WeightMeasurementUnit

object Preferences {
    init {
        PreferenceManager.setDefaultValues(App.context, R.xml.settings, false)
    }

    var introductionShown: Boolean
        get() {
            return getSharedPreferences()
                    .getBoolean(Constants.preferencesIntroductionShown, false)
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putBoolean(Constants.preferencesIntroductionShown, value)
                    .apply()
        }

    var defaultRoutine: String
        get() {
            return getSharedPreferences()
                    .getString(Constants.preferencesDefaultRoutineKey, "routine0")
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putString(Constants.preferencesDefaultRoutineKey, value)
                    .apply()
        }

    var showRestTimer: Boolean
        get() {
            return getSharedPreferences()
                    .getBoolean(Constants.preferencesShowRestTimer, true)
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putBoolean(Constants.preferencesShowRestTimer, value)
                    .apply()
        }

    var showRestTimerAfterWarmup: Boolean
        get() {
            return getSharedPreferences()
                    .getBoolean(Constants.preferencesShowRestTimerAfterWarmup, false)
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putBoolean(Constants.preferencesShowRestTimerAfterWarmup, value)
                    .apply()
        }

    var showRestTimerAfterBodylineDrills: Boolean
        get() {
            return getSharedPreferences()
                    .getBoolean(Constants.preferencesShowRestTimerAfterBodylineDrills, true)
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putBoolean(Constants.preferencesShowRestTimerAfterBodylineDrills, value)
                    .apply()
        }

    var showRestTimerAfterFlexibilityExercises: Boolean
        get() {
            return getSharedPreferences()
                    .getBoolean(Constants.preferencesShowRestTimerAfterFlexibilityExercises, false)
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putBoolean(Constants.preferencesShowRestTimerAfterFlexibilityExercises, value)
                    .apply()
        }

    var restTimerDefaultSeconds: Int
        get() {
            return getSharedPreferences()
                    .getString(Constants.preferencesRestTimerDefaultSeconds, "60").toInt()
        }

        set(value) {
            getSharedPreferences()
                    .edit()
                    .putString(Constants.preferencesRestTimerDefaultSeconds, value.toString())
                    .apply()
        }

    val weightMeasurementUnit: WeightMeasurementUnit
        get() {
            val value = getSharedPreferences().getString(Constants.preferencesWeightMeasurementUnitsKey, "kg")

            if (value.equals("kg", ignoreCase = true)) {
                return WeightMeasurementUnit.Kg
            }

            return WeightMeasurementUnit.Lbs
        }

    fun playSoundWhenTimerStops(): Boolean {
        return getSharedPreferences().getBoolean(Constants.preferencesPlaySoundWhenTimerStopsKey, true)
    }

    fun automaticallyLogWorkoutTime(): Boolean {
        return getSharedPreferences().getBoolean(Constants.preferencesAutomaticallyLogWorkoutTimeKey, true)
    }

    fun keepScreenOnWhenAppIsRunning(): Boolean {
        return getSharedPreferences().getBoolean(Constants.preferencesKeepScreenOnKey, true)
    }

    fun setTimerValue(exerciseId: String, value: Long) {
        getSharedPreferences().edit().putLong(
                String.format("%s%s", Constants.preferencesTimerKey, exerciseId),
                value
        ).apply()
    }

    fun setNumberOfReps(exerciseId: String, value: Int) {
        getSharedPreferences().edit().putInt(
                String.format("%s%s", Constants.preferencesNumberOfRepsKey, exerciseId),
                value
        ).apply()
    }

    fun getTimerValueForExercise(exerciseId: String, defaultValue: Long): Long {
        return getSharedPreferences().getLong(
                String.format("%s%s", Constants.preferencesTimerKey, exerciseId),
                defaultValue
        )
    }

    fun getNumberOfRepsForExercise(exerciseId: String, defaultValue: Int): Int {
        return getSharedPreferences().getInt(
                String.format("%s%s", Constants.preferencesNumberOfRepsKey, exerciseId),
                defaultValue
        )
    }

    fun setExerciseIdForSection(sectionId: String, exerciseId: String) {
        getSharedPreferences().edit().putString(
                String.format("%s%s", Constants.preferencesExerciseIdForSection, sectionId),
                exerciseId
        ).apply()
    }

    fun getExerciseIdForSection(sectionId: String): String? {
        val emptyId = "NULL"
        val exerciseId = getSharedPreferences().getString(
                String.format("%s%s", Constants.preferencesExerciseIdForSection, sectionId),
                emptyId
        )

        return if (exerciseId == emptyId) {
            null
        } else {
            exerciseId
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(App.context)
    }
}
