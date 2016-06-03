package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setInvisible
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.view.AbstractPresenter
import com.bodyweight.fitness.view.AbstractView

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_timer.view.*

class NavigationPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    val view = (view as NavigationView)

                    view.showTimerOrRepsLogger(it.isTimedSet)
                    view.showPreviousNextButtons(it.isPrevious, it.isNext)
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        val view = (view as NavigationView)

        val exercise = RoutineStream.exercise

        view.showTimerOrRepsLogger(exercise.isTimedSet)
        view.showPreviousNextButtons(exercise.isPrevious, exercise.isNext)
    }

    fun previousExercise() {
        if (RoutineStream.exercise.isPrevious) {
            RoutineStream.exercise = RoutineStream.exercise.previous!!
        }
    }

    fun nextExercise() {
        if (RoutineStream.exercise.isNext) {
            RoutineStream.exercise = RoutineStream.exercise.next!!
        }
    }
}

open class NavigationView : AbstractView {
    override var presenter: AbstractPresenter = NavigationPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        prev_exercise_button.setOnClickListener {
            (presenter as NavigationPresenter).previousExercise()
        }

        next_exercise_button.setOnClickListener {
            (presenter as NavigationPresenter).nextExercise()
        }
    }

    fun showTimerOrRepsLogger(isTimed: Boolean) {
        if (isTimed) {
            timer_view.setVisible()
            reps_logger_view.setGone()

            rest_timer_view.setGone()
        } else {
            timer_view.setGone()
            reps_logger_view.setVisible()

            rest_timer_view.setGone()
        }
    }

    fun showPreviousNextButtons(hasPrevious: Boolean, hasNext: Boolean) {
        if (hasPrevious) {
            prev_exercise_button.setVisible()
        } else {
            prev_exercise_button.setInvisible()
        }

        if (hasNext) {
            next_exercise_button.setVisible()
        } else {
            next_exercise_button.setInvisible()
        }
    }
}