package com.bodyweight.fitness

import com.bodyweight.fitness.model.repository.RepositoryRoutine
import com.bodyweight.fitness.stream.RepositoryStream
import org.joda.time.DateTime

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

fun Int.formatMinutes(): String {
    val minutes = this / 60

    if (minutes == 0) {
        return "00"
    } else if (minutes < 10) {
        return "0" + minutes
    }

    return minutes.toString()
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