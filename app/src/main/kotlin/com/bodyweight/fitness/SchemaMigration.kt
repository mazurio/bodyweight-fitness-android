package com.bodyweight.fitness

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.model.repository.RepositoryRoutine
import com.bodyweight.fitness.stream.RepositoryStream
import com.bodyweight.fitness.stream.RoutineStream

class SchemaMigration {
    fun migrateSchemaIfNeeded() {
        if (RepositoryStream.getInstance().repositoryRoutineForTodayExists()) {
            val routine = RoutineStream.getInstance().routine
            val currentSchema = RepositoryStream.getInstance().repositoryRoutineForToday

            migrateSchemaIfNeeded(routine, currentSchema)
        }
    }

    private fun migrateSchemaIfNeeded(routine: Routine, currentSchema: RepositoryRoutine) {
        if (!(isValidSchema(routine, currentSchema))) {
            val newSchema = RepositoryStream.getInstance().buildRealmRoutine(routine)

            val realm = RepositoryStream.getInstance().realm

            realm.executeTransaction {
                newSchema.startTime = currentSchema.startTime
                newSchema.lastUpdatedTime = currentSchema.lastUpdatedTime

                for (exercise in newSchema.exercises) {
                    val currentExercise = currentSchema.exercises.filter {
                        it.exerciseId == exercise.exerciseId
                    }.firstOrNull()

                    if (currentExercise != null) {
                        exercise.sets.removeAll { true }

                        for (set in currentExercise.sets) {
                            exercise.sets.add(set)
                        }
                    }
                }

                currentSchema.removeFromRealm()

                realm.copyToRealmOrUpdate(newSchema)
            }
        }
    }

    private fun isValidSchema(routine: Routine, currentSchema: RepositoryRoutine): Boolean {
        for (exercise in routine.exercises) {
            val contains = currentSchema.exercises.filter {
                it.exerciseId == exercise.exerciseId
            }.firstOrNull()

            contains ?: return false
        }

        return true
    }
}