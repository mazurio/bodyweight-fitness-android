package com.bodyweight.fitness

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