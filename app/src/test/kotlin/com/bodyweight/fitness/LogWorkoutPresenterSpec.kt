package com.bodyweight.fitness

import com.bodyweight.fitness.dialog.LogWorkoutPresenter
import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositorySet
import io.realm.RealmList

import org.jetbrains.spek.api.Spek

import kotlin.properties.Delegates
import kotlin.test.assertEquals

class LogWorkoutPresenterSpec: Spek({
    given("LogWorkoutPresenter") {
        beforeEach {
            repositoryExercise = RepositoryExercise(
                    id = "id",
                    exerciseId = "exerciseId",
                    title = "title",
                    description = "description",
                    defaultSet = "weighted",
                    visible = true,
                    routine = null,
                    category = null,
                    section = null,
                    sets = RealmList()
            )

            logWorkoutPresenter = LogWorkoutPresenter()
        }

        given("toolbar description") {
            it("shows not completed") {
                val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                assertEquals("Not Completed", description)
            }

            given("weighted exercise") {
                it("shows number of sets and total number of reps for 1 set") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 2, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("1 Set, 2 Reps", description)
                }

                it("shows number of sets and total number of reps for 1 set and 1 rep") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 1, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("1 Set, 1 Rep", description)
                }

                it("shows number of sets and total number of reps for 2 sets") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 2, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 4, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("2 Sets, 6 Reps", description)
                }

                it("shows number of sets and total number of reps for 2 sets and 1 rep") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 1, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 0, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("2 Sets, 1 Rep", description)
                }
            }

            given("timed exercise") {
                beforeEach {
                    repositoryExercise.defaultSet = "timed"
                }

                it("shows number of sets and total number of reps for 1 set") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 10, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("1 Set, 10 Seconds", description)
                }

                it("shows number of sets and total number of reps for 1 set and 30 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 30, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("1 Set, 30 Seconds", description)
                }

                it("shows number of sets and total number of reps for 2 sets") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 10, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 20, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("2 Sets, 30 Seconds", description)
                }

                it("shows number of sets and total number of reps for 2 sets and 10 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 10, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("2 Sets, 10 Seconds", description)
                }

                it("shows number of sets and total number of reps for 1 set and 60 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 60, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("1 Set, 1 Minute", description)
                }

                it("shows number of sets and total number of reps for 1 set and 61 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 61, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("1 Set, 1 Minute, 1 Second", description)
                }

                it("shows number of sets and total number of reps for 4 sets and 280 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 10, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 55, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 43, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 80, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

                    assertEquals("4 Sets, 3 Minutes, 8 Seconds", description)
                }
            }
        }

        given("previous workout description") {
            it("shows not completed") {
                val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                assertEquals("Not Completed", description)
            }

            given("weighted exercise") {
                it("shows number of reps for 1 set") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 2, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("1 Set, 2 Reps", description)
                }

                it("shows number of reps for 2 sets") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 2, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 5, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("2-5", description)
                }

                it("shows number of reps for many sets") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 2, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 5, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 5, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 5, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 6, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 7, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = false, weight = 0.0, reps = 3, seconds = 0, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("2-5-5-5-6-7-3", description)
                }
            }

            given("timed exercise") {
                beforeEach {
                    repositoryExercise.defaultSet = "timed"
                }

                it("shows time for 1 set and 10 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 10, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("1 Set, 10 Seconds", description)
                }

                it("shows time for 1 set and 1 second") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 1, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("1 Set, 1 Second", description)
                }

                it("shows time for 1 set and 1 minute") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 60, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("1 Set, 1 Minute", description)
                }

                it("shows time for 1 set and 2 minutes") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 120, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("1 Set, 2 Minutes", description)
                }

                it("shows time for 1 set and 2 minutes 31 seconds") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 151, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("1 Set, 2 Minutes 31 Seconds", description)
                }

                it("shows time for 2 sets") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 45, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 45, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("45s-45s", description)
                }

                it("shows time for multiple sets") {
                    repositoryExercise.sets = RealmList(
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 45, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 45, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 60, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 121, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 1, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 5, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 0, exercise = repositoryExercise),
                            RepositorySet(id = "id", isTimed = true, weight = 0.0, reps = 0, seconds = 60, exercise = repositoryExercise)
                    )

                    val description = logWorkoutPresenter.getPreviousWorkoutDescription(repositoryExercise)

                    assertEquals("45s-45s-60s-121s-1s-5s-0s-60s", description)
                }
            }
        }
    }
}) {
    companion object {
        var repositoryExercise: RepositoryExercise by Delegates.notNull()
        var logWorkoutPresenter: LogWorkoutPresenter = LogWorkoutPresenter()
    }
}