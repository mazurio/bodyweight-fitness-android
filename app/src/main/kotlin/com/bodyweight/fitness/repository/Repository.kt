package com.bodyweight.fitness.repository

import org.joda.time.DateTime

import java.util.UUID

import com.bodyweight.fitness.App
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.stream.RoutineStream

import io.realm.DynamicRealm
import io.realm.DynamicRealmObject
import io.realm.Realm
import io.realm.RealmConfiguration

object Repository {
    private val realmName = "bodyweight.fitness.realm"

    val realm: Realm
        get() = Realm.getInstance(RealmConfiguration.Builder(App.context)
                .name(realmName)
                .schemaVersion(2)
                .migration { realm: DynamicRealm, oldVersion: Long, newVersion: Long ->
                    val schema = realm.schema
                    val routineSchema = schema.get("RepositoryRoutine")

                    if (oldVersion.toInt() == 1) {
                        routineSchema
                                .addField("title", String::class.java)
                                .addField("subtitle", String::class.java)
                                .transform { obj: DynamicRealmObject ->
                                    obj.set("title", "Bodyweight Fitness")
                                    obj.set("subtitle", "Recommended Routine")
                                }
                    }
                }
                .build())

    fun buildRealmRoutine(routine: Routine): RepositoryRoutine {
        var repositoryRoutine: RepositoryRoutine? = null

        realm.executeTransaction {
            repositoryRoutine = realm.createObject(RepositoryRoutine::class.java)
            repositoryRoutine?.let {
                it.id = "Routine-" + UUID.randomUUID().toString()
                it.routineId = routine.routineId
                it.title = routine.title
                it.subtitle = routine.subtitle
                it.startTime = DateTime().toDate()
                it.lastUpdatedTime = DateTime().toDate()

                var repositoryCategory: RepositoryCategory? = null
                var repositorySection: RepositorySection? = null

                for (exercise in routine.exercises) {
                    val repositoryExercise = realm.createObject(RepositoryExercise::class.java)
                    repositoryExercise.id = "Exercise-" + UUID.randomUUID().toString()
                    repositoryExercise.exerciseId = exercise.exerciseId
                    repositoryExercise.title = exercise.title
                    repositoryExercise.description = exercise.description
                    repositoryExercise.defaultSet = exercise.defaultSet

                    val repositorySet = realm.createObject(RepositorySet::class.java)
                    repositorySet.id = "Set-" + UUID.randomUUID().toString()

                    if (exercise.defaultSet == "weighted") {
                        repositorySet.isTimed = false
                    } else {
                        repositorySet.isTimed = true
                    }

                    repositorySet.seconds = 0
                    repositorySet.weight = 0.0
                    repositorySet.reps = 0
                    repositorySet.exercise = repositoryExercise

                    repositoryExercise.sets.add(repositorySet)

                    if (repositoryCategory == null || !repositoryCategory.title.equals(exercise.category!!.title, ignoreCase = true)) {
                        repositoryCategory = realm.createObject(RepositoryCategory::class.java)
                        repositoryCategory!!.id = "Category-" + UUID.randomUUID().toString()
                        repositoryCategory.categoryId = exercise.category!!.categoryId
                        repositoryCategory.title = exercise.category!!.title
                        repositoryCategory.routine = repositoryRoutine

                        it.categories.add(repositoryCategory)
                    }

                    if (repositorySection == null || !repositorySection.title.equals(exercise.section!!.title, ignoreCase = true)) {
                        repositorySection = realm.createObject(RepositorySection::class.java)
                        repositorySection!!.id = "Section-" + UUID.randomUUID().toString()
                        repositorySection.sectionId = exercise.section!!.sectionId
                        repositorySection.title = exercise.section!!.title
                        repositorySection.mode = exercise.section!!.sectionMode.toString()
                        repositorySection.routine = repositoryRoutine
                        repositorySection.category = repositoryCategory

                        it.sections.add(repositorySection)
                        repositoryCategory.sections.add(repositorySection)
                    }

                    repositoryExercise.routine = repositoryRoutine
                    repositoryExercise.category = repositoryCategory
                    repositoryExercise.section = repositorySection

                    /**
                     * Hide exercises not relevant to user level.
                     */
                    if (exercise.section!!.sectionMode == SectionMode.Levels || exercise.section!!.sectionMode == SectionMode.Pick) {
                        if (exercise == exercise.section!!.currentExercise) {
                            repositoryExercise.visible = true
                        } else {
                            repositoryExercise.visible = false
                        }
                    } else {
                        repositoryExercise.visible = true
                    }

                    it.exercises.add(repositoryExercise)
                    repositoryCategory.exercises.add(repositoryExercise)
                    repositorySection.exercises.add(repositoryExercise)
                }
            }
        }

        return repositoryRoutine!!
    }

    fun getRepositoryRoutineForPrimaryKeyRoutineId(primaryKeyRoutineId: String): RepositoryRoutine {
        return realm.where(RepositoryRoutine::class.java).equalTo("id", primaryKeyRoutineId).findFirst()
    }

    val repositoryRoutineForToday: RepositoryRoutine
        get() {
            val start = DateTime().withTimeAtStartOfDay().toDate()
            val end = DateTime().withTimeAtStartOfDay().plusDays(1).minusSeconds(1).toDate()

            val routineId = RoutineStream.routine.routineId

            var repositoryRoutine: RepositoryRoutine? = realm.where(RepositoryRoutine::class.java)
                    .between("startTime", start, end)
                    .equalTo("routineId", routineId)
                    .findFirst()

            if (repositoryRoutine == null) {
                repositoryRoutine = buildRealmRoutine(RoutineStream.routine)
            }

            return repositoryRoutine
        }

    fun repositoryRoutineForTodayExists(): Boolean {
        val start = DateTime().withTimeAtStartOfDay().toDate()
        val end = DateTime().withTimeAtStartOfDay().plusDays(1).minusSeconds(1).toDate()

        val routineId = RoutineStream.routine.routineId

        realm.where(RepositoryRoutine::class.java)
                .between("startTime", start, end)
                .equalTo("routineId", routineId)
                .findFirst() ?: return false

        return true
    }
}
