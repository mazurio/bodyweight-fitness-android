package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.stream.RoutineStream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_timer.view.*

class NavigationPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    val view = (view as NavigationView)

                    view.showOrHideButtons(it.isPrevious, it.isNext)
                    view.showTimerOrRepsLogger(it.isTimedSet)
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        (view as NavigationView).showOrHideButtons(RoutineStream.exercise.isPrevious, RoutineStream.exercise.isNext)
    }

    fun previousExercise() {
        RoutineStream.exercise = RoutineStream.exercise.previous!!
    }

    fun nextExercise() {
        RoutineStream.exercise = RoutineStream.exercise.next!!
    }
}

open class NavigationView : AbstractView {
    override var mPresenter: AbstractPresenter = NavigationPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        prev_exercise_button.setOnClickListener {
            (mPresenter as NavigationPresenter).previousExercise()
        }

        next_exercise_button.setOnClickListener {
            (mPresenter as NavigationPresenter).nextExercise()
        }
    }

    fun showOrHideButtons(isPrevious: Boolean, isNext: Boolean) {
        if (isPrevious) {
            prev_exercise_button.visibility = View.VISIBLE
        } else {
            prev_exercise_button.visibility = View.INVISIBLE
        }

        if (isNext) {
            next_exercise_button.visibility = View.VISIBLE
        } else {
            next_exercise_button.visibility = View.INVISIBLE
        }
    }

    fun showTimerOrRepsLogger(isTimed: Boolean) {
        if (isTimed) {
            timer_view.visibility = View.VISIBLE
            reps_logger_view.visibility = View.GONE
        } else {
            timer_view.visibility = View.GONE
            reps_logger_view.visibility = View.VISIBLE
        }
    }
}