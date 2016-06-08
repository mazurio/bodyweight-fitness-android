package com.bodyweight.fitness

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.utils.Preferences

import io.realm.RealmResults
import org.joda.time.DateTime

fun primary(): Int {
    return Color.parseColor("#009688")
}

fun primaryDark(): Int {
    return Color.parseColor("#00453E")
}

fun calculateLayoutWeight(completionRate: Int): Float {
    if (completionRate <= 10) {
        return 7f
    }

    val weight = completionRate * 0.7f;

    if (weight > 70f) {
        return 70f
    }

    return weight
}

fun LinearLayout.setLayoutWeight(weight: Float) {
    val params = this.layoutParams as LinearLayout.LayoutParams

    params.weight = weight;

    this.layoutParams = params
}

fun Int.toPx(context: Context): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()

fun View.setBackgroundResourceWithPadding(resource: Int) {
    val bottom = paddingBottom
    val top = paddingTop
    val right = paddingRight
    val left = paddingLeft

    setBackgroundResource(resource)
    setPadding(left, top, right, bottom)
}

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.inflate(layoutRes: Int, root: ViewGroup, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, root, attachToRoot)
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun DateTime.isToday(): Boolean {
    val now = DateTime()

    return now.year == this.year &&
            now.monthOfYear == this.monthOfYear &&
            now.dayOfMonth == this.dayOfMonth
}

fun DateTime.isRoutineLogged(): Boolean {
    val start = this.withTimeAtStartOfDay()
            .toDate()

    val end = this.withTimeAtStartOfDay()
            .plusDays(1)
            .minusSeconds(1)
            .toDate()

    val realm = Repository.realm
    val routine = realm.where(RepositoryRoutine::class.java)
            .between("startTime", start, end)
            .findFirst()

    return routine != null
}

fun DateTime.isRoutineLoggedWithResults(): RealmResults<RepositoryRoutine> {
    val start = this.withTimeAtStartOfDay()
            .toDate()

    val end = this.withTimeAtStartOfDay()
            .plusDays(1)
            .minusSeconds(1)
            .toDate()

    val realm = Repository.realm
    val results: RealmResults<RepositoryRoutine> = realm.where(RepositoryRoutine::class.java)
            .between("startTime", start, end)
            .findAll()

    return results
}

fun Double.formatWeight(): String {
    return "$this ${Preferences.weightMeasurementUnit.asString}"
}

fun Int.formatReps(append: Boolean = false): String {
    if (append) {
        return "$this x"
    } else {
        if (this == 0) {
            return "/"
        }

        return this.toString()
    }
}

fun Int.formatMinutes(format: Boolean = true): String {
    val minutes = this / 60

    if (minutes == 0) {
        if (format) {
            return "00"
        }

        return "0"
    } else if (minutes < 10) {
        if (format) {
            return "0" + minutes
        }

        return minutes.toString()
    }

    return minutes.toString()
}

fun Int.formatMinutesPostfix(): String {
    val minutes = this / 60

    if (minutes == 0) {
        return "0m"
    }

    return "${minutes.toString()}m"
}

fun Int.formatMinutesAsNumber(): Int {
    return this / 60
}

fun Int.formatSeconds(format: Boolean = true): String {
    val seconds = this % 60

    if (seconds == 0) {
        if (format) {
            return "00"
        }

        return "0"
    } else if (seconds < 10) {
        if (format) {
            return "0" + seconds
        }

        return seconds.toString()
    }

    return seconds.toString()
}

fun Int.formatSecondsPostfix(): String {
    val seconds = this % 60

    if (seconds == 0) {
        return "0s"
    }

    return "${seconds.toString()}s"
}

fun Int.formatSecondsAsNumber(): Int {
    return this % 60
}