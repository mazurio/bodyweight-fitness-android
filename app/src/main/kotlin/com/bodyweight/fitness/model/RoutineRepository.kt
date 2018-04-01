package com.bodyweight.fitness.model

import com.bodyweight.fitness.formatMinutes
import com.bodyweight.fitness.formatSeconds
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.utils.Preferences

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Duration
import org.joda.time.Minutes
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
    fun setLastUpdatedTime(repositoryRoutine: RepositoryRoutine, isNestedTransaction: Boolean) {
      val now = DateTime.now()

      val startTime = DateTime(repositoryRoutine.startTime)
      val lastUpdatedTime = DateTime(repositoryRoutine.lastUpdatedTime)

      /**
       * Set last updated time only if it's the same day.
       * Set last updated time only if the difference in minutes is less than 180.
       */
      if (Days.daysBetween(startTime.toLocalDate(), now.toLocalDate()).days == 0) {
        if (Minutes.minutesBetween(startTime.toLocalDate(), lastUpdatedTime.toLocalDate()).minutes < 180) {
          if (isNestedTransaction) {
            repositoryRoutine.lastUpdatedTime = DateTime().toDate()
          } else {
            Repository.realm.executeTransaction {
              repositoryRoutine.lastUpdatedTime = DateTime().toDate()
            }
          }
        }
      }
    }

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

      val formatter = PeriodFormatterBuilder()
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

    fun getTitleWithDate(repositoryRoutine: RepositoryRoutine): String {
      val date = DateTime(repositoryRoutine.startTime).toString("EEEE, d MMMM YYYY - HH:mm")

      return "${repositoryRoutine.title} - ${repositoryRoutine.subtitle} - $date."
    }

    fun toText(repositoryRoutine: RepositoryRoutine): String {
      val startTime = DateTime(repositoryRoutine.startTime).toString("EEEE, d MMMM YYYY - HH:mm")
      val lastUpdatedTime = RepositoryRoutine.getLastUpdatedTime(repositoryRoutine)
      val workoutLength = RepositoryRoutine.getWorkoutLength(repositoryRoutine)
      val weightUnit = Preferences.weightMeasurementUnit.toString()

      var content = "Hello, The following is your workout in Text/HTML format (CSV attached)."

      content += "\n\nWorkout on $startTime."
      content += "\nLast Updated at $lastUpdatedTime."
      content += "\nWorkout length: $workoutLength"
      content += "\n\n${repositoryRoutine.title} - ${repositoryRoutine.subtitle}"

      for (exercise in RepositoryRoutine.getVisibleAndCompletedExercises(repositoryRoutine.exercises)) {
        content += "\n\n${exercise.title}"

        for ((index, set) in exercise.sets.withIndex()) {
          content += "\nSet ${index + 1}"

          if (set.isTimed) {
            content += "\nMinutes: ${set.seconds.formatMinutes(false)}"
            content += "\nSeconds: ${set.seconds.formatSeconds(false)}"
          } else {
            content += "\nReps: ${set.reps}"
            content += "\nWeight: ${set.weight} $weightUnit"
          }
        }
      }

      return content
    }

    fun toCSV(repositoryRoutine: RepositoryRoutine): String {
      val date = DateTime(repositoryRoutine.startTime).toString("d MMMM YYYY")
      val startTime = getStartTime(repositoryRoutine)
      val lastUpdatedTime = getLastUpdatedTime(repositoryRoutine)
      val workoutLength = getWorkoutLength(repositoryRoutine)
      val routineTitle = "${repositoryRoutine.title} - ${repositoryRoutine.subtitle}"
      val weightUnit = Preferences.weightMeasurementUnit.toString()

      var content = "Date, Start Time, End Time, Workout Length, Routine, Exercise, Set Order, Reps, Weight, Minutes, Seconds\n"

      for (exercise in getVisibleAndCompletedExercises(repositoryRoutine.exercises)) {
        for ((index, set) in exercise.sets.withIndex()) {
          content += "$date,$startTime,$lastUpdatedTime,$workoutLength,$routineTitle,${exercise.title},${index + 1},${set.reps},${set.weight} $weightUnit,${set.seconds.formatMinutes(false)},${set.seconds.formatSeconds(false)}\n"
        }
      }

      return content
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
  open var exercises: RealmList<RepositoryExercise> = RealmList(),
  open var bundle: Int? = null
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

  open var exercises: RealmList<RepositoryExercise> = RealmList(),
  open var bundle: Int = 0,
  open var sets: Int = 1
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
