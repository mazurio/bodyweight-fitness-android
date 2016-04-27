package com.bodyweight.fitness

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bodyweight.fitness.model.repository.RepositoryRoutine
import com.bodyweight.fitness.stream.RepositoryStream
import io.realm.RealmResults
import org.joda.time.DateTime

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
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

    val realm = RepositoryStream.getInstance().realm
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

    val realm = RepositoryStream.getInstance().realm
    val results: RealmResults<RepositoryRoutine> = realm.where(RepositoryRoutine::class.java)
            .between("startTime", start, end)
            .findAll()

    return results
}

fun Int.formatMinutes(): String {
    val minutes = this / 60

    if (minutes == 0) {
        return "00"
    } else if (minutes < 10) {
        return "0" + minutes
    }

    return minutes.toString()
}

fun Int.formatMinutesAsNumber(): Int {
    return this / 60
}

fun Int.formatSeconds(): String {
    val seconds = this % 60

    if (seconds == 0) {
        return "00"
    } else if (seconds < 10) {
        return "0" + seconds
    }

    return seconds.toString()
}

fun Int.formatSecondsAsNumber(): Int {
    return this % 60
}