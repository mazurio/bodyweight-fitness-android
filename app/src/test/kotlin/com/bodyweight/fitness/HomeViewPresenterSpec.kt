package com.bodyweight.fitness

import com.bodyweight.fitness.model.RepositoryCategory
import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.model.RepositorySet
import com.bodyweight.fitness.view.HomeViewPresenter

import io.realm.RealmList

import org.jetbrains.spek.api.Spek
import org.joda.time.DateTime

import kotlin.properties.Delegates
import kotlin.test.assertEquals

class HomeViewPresenterSpec: Spek({
    given("HomeViewPresenter") {
        beforeEach {
            repositoryRoutine = RepositoryRoutine(
                    id = "id",
                    routineId = "routineId",
                    title = "title",
                    subtitle = "subtitle",
                    startTime = DateTime().toDate(),
                    lastUpdatedTime = DateTime().toDate()
            )

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

            homeViewPresenter = HomeViewPresenter()
        }

        given("start working out button") {
            it("says 'Start Working Out' if repository routine does not exist") {
                assertEquals("Start Working Out", homeViewPresenter.getStartWorkoutButtonText(false))
            }

            it("says 'Workout' if repository routine does not exist") {
                assertEquals("Go to Workout", homeViewPresenter.getStartWorkoutButtonText(true))
            }
        }

        given("today's progress") {
            it("calculates layout weight of 7f for 0% completion rate") {
                val layoutWeight = homeViewPresenter.calculateLayoutWeight(0)

                assertEquals(7f, layoutWeight)
            }

            it("calculates layout weight of 7f for 3% completion rate") {
                val layoutWeight = homeViewPresenter.calculateLayoutWeight(3)

                assertEquals(7f, layoutWeight)
            }

            it("calculates layout weight of 35f for 50% completion rate") {
                val layoutWeight = homeViewPresenter.calculateLayoutWeight(50)

                assertEquals(35f, layoutWeight)
            }

            it("calculates layout weight of 70f for 150% completion rate") {
                val layoutWeight = homeViewPresenter.calculateLayoutWeight(150)

                assertEquals(70f, layoutWeight)
            }

            it("shows 0% completion rate for category of no exercises") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList()
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(0, completionRate.percentage)
                assertEquals("0%", completionRate.label)
            }

            it("shows 0% completion rate for category") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList()
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList()
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(0, completionRate.percentage)
                assertEquals("0%", completionRate.label)
            }

            it("shows 50% completion rate for category of two timed exercises") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList()
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(50, completionRate.percentage)
                assertEquals("50%", completionRate.label)
            }

            it("shows 50% completion rate for category of two weighted exercises") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 10,
                                                        weight = 20.0,
                                                        seconds = 0
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList()
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(50, completionRate.percentage)
                assertEquals("50%", completionRate.label)
            }

            it("shows 100% completion rate") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 45
                                                )
                                        )
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(100, completionRate.percentage)
                assertEquals("100%", completionRate.label)
            }

            it("shows 66% completion rate") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 0
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 45
                                                )
                                        )
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(66, completionRate.percentage)
                assertEquals("66%", completionRate.label)
            }

            it("shows 75% completion rate") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 0
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 45
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 45
                                                )
                                        )
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(75, completionRate.percentage)
                assertEquals("75%", completionRate.label)
            }

            it("shows 75% completion rate for visible exercises only") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 0
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 45
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = false,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 0
                                                )
                                        )
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(66, completionRate.percentage)
                assertEquals("66%", completionRate.label)
            }

            it("shows 75% completion rate for exercises which are completed") {
                val repositoryCategory = RepositoryCategory(
                        exercises = RealmList(
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 0
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = true,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 45
                                                )
                                        )
                                ),
                                RepositoryExercise(
                                        visible = false,
                                        sets = RealmList(
                                                RepositorySet(
                                                        reps = 0,
                                                        weight = 0.0,
                                                        seconds = 40
                                                )
                                        )
                                )
                        )
                )

                val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

                assertEquals(75, completionRate.percentage)
                assertEquals("75%", completionRate.label)
            }
        }
    }
}) {
    companion object {
        var repositoryRoutine: RepositoryRoutine by Delegates.notNull()
        var repositoryExercise: RepositoryExercise by Delegates.notNull()
        var homeViewPresenter: HomeViewPresenter = HomeViewPresenter()
    }
}