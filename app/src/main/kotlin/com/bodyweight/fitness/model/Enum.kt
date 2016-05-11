package com.bodyweight.fitness.model

import com.bodyweight.fitness.adapter.CalendarAdapter
import org.joda.time.DateTime
import java.io.Serializable

data class CalendarDay(var page: Int = 60, var day: Int = 3) {
    fun getDate(): DateTime {
        if (this.page == CalendarAdapter.DEFAULT_POSITION) {
            return DateTime()
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.day)
        } else if (this.page < CalendarAdapter.DEFAULT_POSITION) {
            return DateTime()
                    .minusWeeks(CalendarAdapter.DEFAULT_POSITION - this.page)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.day)
        }

        return DateTime()
                .plusWeeks(this.page - CalendarAdapter.DEFAULT_POSITION)
                .dayOfWeek()
                .withMinimumValue()
                .plusDays(this.day)
    }
}

enum class WeightMeasurementUnit constructor(val asString: String) {
    kg("kg"), lbs("lbs")
}

enum class RoutineType : Serializable {
    CATEGORY, SECTION, EXERCISE, EXERCISE_ACTIVE;
}

enum class SectionMode(val asString: String) {
    ALL("all"), PICK("pick"), LEVELS("levels")
}