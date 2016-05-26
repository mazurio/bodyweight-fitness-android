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

data class DateTimeWorkoutLength(val dateTime: DateTime, val repositoryRoutine: RepositoryRoutine? = null)

data class CompletionRate(val percentage: Int, val label: String)

enum class WeightMeasurementUnit constructor(val asString: String) {
    Kg("kg"), Lbs("lbs")
}

enum class RoutineType : Serializable {
    Category, Section, Exercise, ExerciseActive;
}

enum class SectionMode(val asString: String) {
    All("all"), Pick("pick"), Levels("levels")
}