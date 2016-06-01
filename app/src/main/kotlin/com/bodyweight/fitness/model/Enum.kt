package com.bodyweight.fitness.model

import com.bodyweight.fitness.adapter.CalendarPagerAdapter
import org.joda.time.DateTime
import java.io.Serializable

data class CalendarDay(var page: Int = 60, var day: Int = 3) {
    fun getDate(): DateTime {
        if (this.page == CalendarPagerAdapter.DEFAULT_POSITION) {
            return DateTime()
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.day)
        } else if (this.page < CalendarPagerAdapter.DEFAULT_POSITION) {
            return DateTime()
                    .minusWeeks(CalendarPagerAdapter.DEFAULT_POSITION - this.page)
                    .dayOfWeek()
                    .withMinimumValue()
                    .plusDays(this.day)
        }

        return DateTime()
                .plusWeeks(this.page - CalendarPagerAdapter.DEFAULT_POSITION)
                .dayOfWeek()
                .withMinimumValue()
                .plusDays(this.day)
    }
}

data class DateTimeRepositoryExercise(val dateTime: DateTime, val repositoryExercise: RepositoryExercise? = null)
data class DateTimeRepositorySet(val dateTime: DateTime, val repositorySet: RepositorySet? = null)

data class DateTimeWorkoutLength(val dateTime: DateTime, val repositoryRoutine: RepositoryRoutine? = null)
data class DateTimeCompletionRate(val dateTime: DateTime, val repositoryRoutine: RepositoryRoutine? = null)
data class CategoryDateTimeCompletionRate(val dateTime: DateTime, val repositoryCategory: RepositoryCategory? = null)

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