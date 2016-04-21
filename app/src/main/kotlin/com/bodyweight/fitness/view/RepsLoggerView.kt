package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import com.bodyweight.fitness.model.Exercise

import com.bodyweight.fitness.presenter.AbstractPresenter
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.utils.PreferenceUtils

import kotlinx.android.synthetic.main.view_reps_logger.view.*

class RepsLoggerPresenter : AbstractPresenter() {
    var mExercise: Exercise? = null
    var mNumberOfReps: Int = 5

    override fun bindView(view: AbstractView) {
        super.bindView(view)

        val repsLoggerView: RepsLoggerView = (mView as RepsLoggerView)

        subscribe(RoutineStream.getInstance().exerciseObservable.subscribe {
            mExercise = it

            mNumberOfReps = PreferenceUtils.getInstance()
                    .getNumberOfRepsForExercise(it.exerciseId, 5)

            updateLabels()
            repsLoggerView.showOrHidePreviousAndNextExerciseButtons(it.isPrevious, it.isNext)
        })
    }

    fun updateLabels() {
        val repsLoggerView: RepsLoggerView = (mView as RepsLoggerView)

        repsLoggerView.setNumberOfReps(formatNumberOfReps(mNumberOfReps))

        PreferenceUtils.getInstance()
                .setNumberOfReps(mExercise!!.exerciseId, mNumberOfReps)
    }

    fun increaseReps() {
        if (mNumberOfReps < 25) {
            mNumberOfReps += 1

            updateLabels()
        }
    }

    fun decreaseReps() {
        if (mNumberOfReps > 1) {
            mNumberOfReps -= 1

            updateLabels()
        }
    }

    fun previousExercise() {
        RoutineStream.getInstance().setExercise(mExercise!!.previous)
    }

    fun nextExercise() {
        RoutineStream.getInstance().setExercise(mExercise!!.next)
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
    override var mAbstractPresenter: AbstractPresenter = RepsLoggerPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val repsLoggerPresenter: RepsLoggerPresenter = (mAbstractPresenter as RepsLoggerPresenter)

        increase_reps_button.setOnClickListener {
            repsLoggerPresenter.increaseReps()
        }

        decrease_reps_button.setOnClickListener {
            repsLoggerPresenter.decreaseReps()
        }

        prev_exercise_button.setOnClickListener {
            repsLoggerPresenter.previousExercise()
        }

        next_exercise_button.setOnClickListener {
            repsLoggerPresenter.nextExercise()
        }
    }

    fun setNumberOfReps(numberOfReps: String) {
        reps_logger_reps.text = numberOfReps
    }

    fun showOrHidePreviousAndNextExerciseButtons(hasPrevious: Boolean, hasNext: Boolean) {
        if (hasPrevious) {
            prev_exercise_button.visibility = VISIBLE
        } else {
            prev_exercise_button.visibility = INVISIBLE
        }

        if (hasNext) {
            next_exercise_button.visibility = VISIBLE
        } else {
            next_exercise_button.visibility = INVISIBLE
        }
    }
}