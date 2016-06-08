package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.model.*

import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_timer.view.*
import java.util.*

class RepsLoggerPresenter : AbstractPresenter() {
    var numberOfReps: Int = 5

    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    numberOfReps = Preferences.getNumberOfRepsForExercise(it.exerciseId, 5)

                    updateLabels()
                }

        Stream.repositoryObservable
                .bindToLifecycle(view)
                .subscribe {
                    updateLabels()
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        numberOfReps = Preferences.getNumberOfRepsForExercise(RoutineStream.exercise.exerciseId, 5)

        updateLabels()
    }

    fun updateLabels() {
        val repsLoggerView: RepsLoggerView = (mView as RepsLoggerView)

        repsLoggerView.setSets(formatSets())
        repsLoggerView.setNumberOfReps(formatNumberOfReps(numberOfReps))

        Preferences.setNumberOfReps(RoutineStream.exercise.exerciseId, numberOfReps)
    }

    fun logReps() {
        val realm = Repository.realm
        val repositoryRoutine = Repository.repositoryRoutineForToday

        realm.executeTransaction {
            repositoryRoutine.exercises.filter {
                it.exerciseId == RoutineStream.exercise.exerciseId
            }.firstOrNull()?.let {
                val numberOfSets = it.sets.size

                if (numberOfSets < Constants.maximumNumberOfSets) {
                    val firstSet = it.sets.first()

                    if (numberOfSets == 1 && firstSet.reps == 0) {
                        firstSet.reps = numberOfReps

                        Stream.setLoggedSetReps(SetReps(numberOfSets, numberOfReps))
                    } else {
                        val repositorySet = realm.createObject(RepositorySet::class.java)

                        repositorySet.id = "Set-" + UUID.randomUUID().toString()
                        repositorySet.isTimed = false
                        repositorySet.seconds = 0
                        repositorySet.weight = 0.0
                        repositorySet.reps = numberOfReps

                        repositorySet.exercise = it

                        it.sets.add(repositorySet)

                        Stream.setRepository()
                        Stream.setLoggedSetReps(SetReps(numberOfSets + 1, numberOfReps))
                    }

                    RepositoryRoutine.setLastUpdatedTime(repositoryRoutine, isNestedTransaction = true)
                }
            }
        }

        updateLabels()
    }

    fun increaseReps() {
        if (numberOfReps < 25) {
            numberOfReps += 1

            updateLabels()
        }
    }

    fun decreaseReps() {
        if (numberOfReps > 1) {
            numberOfReps -= 1

            updateLabels()
        }
    }

    fun formatSets(): String {
        val exists = Repository.repositoryRoutineForTodayExists()

        if (exists) {
            val routine = Repository.repositoryRoutineForToday

            routine.exercises.filter {
                it.exerciseId == RoutineStream.exercise.exerciseId
            }.firstOrNull()?.let {
                val sets = it.sets

                if (sets.size == 1 && sets.first().reps == 0) {
                    return "First Set"
                } else if (sets.size >= Constants.maximumNumberOfSets) {
                    return "12 Sets"
                } else if (sets.size >= 5) {
                    return "Set ${sets.count() + 1}"
                }

                var str = ""

                for (set in sets) {
                    str += set.reps.toString() + "-"
                }

                str += "X"

                return str
            }
        }

        return "First Set"
    }

    fun formatNumberOfReps(numberOfReps: Int): String {
        if (numberOfReps == 0) {
            return "00"
        } else if (numberOfReps < 10) {
            return "0" + numberOfReps
        }

        return numberOfReps.toString()
    }
}

open class RepsLoggerView : AbstractView {
    override var presenter: AbstractPresenter = RepsLoggerPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val repsLoggerPresenter: RepsLoggerPresenter = (presenter as RepsLoggerPresenter)

        log_reps_button.setOnClickListener {
            repsLoggerPresenter.logReps()
        }

        increase_reps_button.setOnClickListener {
            repsLoggerPresenter.increaseReps()
        }

        decrease_reps_button.setOnClickListener {
            repsLoggerPresenter.decreaseReps()
        }
    }

    override fun onCreateView() {}

    fun setSets(sets: String) {
        reps_logger_sets.text = sets
    }

    fun setNumberOfReps(numberOfReps: String) {
        reps_logger_reps.text = numberOfReps
    }
}