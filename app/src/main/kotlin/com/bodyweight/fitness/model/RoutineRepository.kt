package com.bodyweight.fitness.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder

import java.util.*

open class RepositoryRoutine(
        @PrimaryKey @Required
        open var id: String = "",

        @Index
        open var routineId: String = "routine0",

        open var title: String = "",
        open var subtitle: String = "",

        @Index
        open var startTime: Date = Date(),

        @Index
        open var lastUpdatedTime: Date = Date(),

        open var categories: RealmList<RepositoryCategory> = RealmList(),
        open var sections: RealmList<RepositorySection> = RealmList(),
        open var exercises: RealmList<RepositoryExercise> = RealmList()
) : RealmObject() {
    companion object {
        fun getStartTime(repositoryRoutine: RepositoryRoutine): String {
            return DateTime(repositoryRoutine.startTime)
                    .toString("HH:mm", Locale.ENGLISH)
        }

        fun getLastUpdatedTime(repositoryRoutine: RepositoryRoutine): String {
            return DateTime(repositoryRoutine.lastUpdatedTime)
                    .toString("HH:mm", Locale.ENGLISH)
        }

        fun getWorkoutLength(repositoryRoutine: RepositoryRoutine): String {
            val duration = Duration(
                    DateTime(repositoryRoutine.startTime),
                    DateTime(repositoryRoutine.lastUpdatedTime))

            val formatter =  PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix("h ")
                    .appendMinutes()
                    .appendSuffix("m")
                    .toFormatter()
                    .print(duration.toPeriod())

            if (formatter.replace(" ", "").equals("")) {
                return "--"
            } else {
                return formatter
            }
        }

        fun getWorkoutLengthInMinutes(repositoryRoutine: RepositoryRoutine): Int {
            return Duration(
                    DateTime(repositoryRoutine.startTime),
                    DateTime(repositoryRoutine.lastUpdatedTime)
            ).toStandardMinutes().minutes
        }

        fun getVisibleAndCompletedExercises(exercises: List<RepositoryExercise>): List<RepositoryExercise> {
            return exercises.filter {
                it.visible || RepositoryExercise.isCompleted(it)
            }
        }

        fun getNumberOfExercises(exercises: List<RepositoryExercise>): Int {
            return getVisibleAndCompletedExercises(exercises).count()
        }

        fun getNumberOfCompletedExercises(exercises: List<RepositoryExercise>): Int {
            return exercises.filter {
                RepositoryExercise.isCompleted(it)
            }.count()
        }

        fun getMissedExercises(exercises: List<RepositoryExercise>): List<RepositoryExercise> {
            return exercises.filter {
                it.visible && !RepositoryExercise.isCompleted(it)
            }
        }

        fun getCompletionRate(repositoryRoutine: RepositoryRoutine): CompletionRate {
            val exercises = getVisibleAndCompletedExercises(repositoryRoutine.exercises)

            val numberOfExercises: Int = getNumberOfExercises(exercises)
            val numberOfCompletedExercises: Int = getNumberOfCompletedExercises(exercises)

            if (numberOfExercises == 0) {
                return CompletionRate(0, "0%")
            }

            val completionRate = numberOfCompletedExercises * 100 / numberOfExercises

            return CompletionRate(completionRate, "$completionRate%")
        }
    }
}

open class RepositoryCategory(
        @PrimaryKey @Required
        open var id: String = "",

        @Index
        open var categoryId: String = "",

        open var title: String = "",

        open var routine: RepositoryRoutine? = null,

        open var sections: RealmList<RepositorySection> = RealmList(),
        open var exercises: RealmList<RepositoryExercise> = RealmList()
) : RealmObject() {
    companion object {
        fun getCompletionRate(repositoryCategory: RepositoryCategory): CompletionRate {
            val exercises = RepositoryRoutine.getVisibleAndCompletedExercises(repositoryCategory.exercises)

            val numberOfExercises: Int = RepositoryRoutine.getNumberOfExercises(exercises)
            val numberOfCompletedExercises: Int = RepositoryRoutine.getNumberOfCompletedExercises(exercises)

            if (numberOfExercises == 0) {
                return CompletionRate(0, "0%")
            }

            val completionRate = numberOfCompletedExercises * 100 / numberOfExercises

            return CompletionRate(completionRate, "$completionRate%")
        }
    }
}

open class RepositorySection(
        @PrimaryKey @Required
        open var id: String = "",

        @Index
        open var sectionId: String = "",

        open var title: String = "",
        open var mode: String = "",

        open var routine: RepositoryRoutine? = null,
        open var category: RepositoryCategory? = null,

        open var exercises: RealmList<RepositoryExercise> = RealmList()
) : RealmObject() {}

open class RepositoryExercise(
        @PrimaryKey @Required
        open var id: String = "",

        @Index open var exerciseId: String = "",

        open var title: String = "",
        open var description: String = "",
        open var defaultSet: String = "timed",

        open var visible: Boolean = false,

        open var routine: RepositoryRoutine? = null,
        open var category: RepositoryCategory? = null,
        open var section: RepositorySection? = null,

        open var sets: RealmList<RepositorySet> = RealmList()
) : RealmObject() {
    companion object {
        fun isCompleted(repositoryExercise: RepositoryExercise): Boolean {
            val size = repositoryExercise.sets.size

            if (size == 0) {
                return false
            }

            val firstSet = repositoryExercise.sets[0]

            if (size == 1 && firstSet.seconds == 0 && firstSet.reps == 0) {
                return false
            }

            return true
        }
    }
}

open class RepositorySet(
        @PrimaryKey @Required
        open var id: String = "",

        open var isTimed: Boolean = false,

        open var weight: Double = 0.0,
        open var reps: Int = 0,
        open var seconds: Int = 0,

        open var exercise: RepositoryExercise? = null
) : RealmObject() {}
