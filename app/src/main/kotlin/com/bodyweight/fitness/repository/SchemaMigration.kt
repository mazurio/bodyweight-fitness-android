package com.bodyweight.fitness.repository

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.stream.RoutineStream

class SchemaMigration {
    fun migrateSchemaIfNeeded() {
        if (Repository.repositoryRoutineForTodayExists()) {
            val routine = RoutineStream.routine
            val currentSchema = Repository.repositoryRoutineForToday

            migrateSchemaIfNeeded(routine, currentSchema)
        }
    }

    private fun migrateSchemaIfNeeded(routine: Routine, currentSchema: RepositoryRoutine) {
        if (!(isValidSchema(routine, currentSchema))) {
            val newSchema = Repository.buildRealmRoutine(routine)

            val realm = Repository.realm

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