package com.bodyweight.fitness.extension

import android.util.Log

val TAG = "Bodyweight Fitness"

fun Any.debug(text: String) {
    Log.d(TAG, text)
}

fun Any.error(text: String) {
    Log.e(TAG, text)
}