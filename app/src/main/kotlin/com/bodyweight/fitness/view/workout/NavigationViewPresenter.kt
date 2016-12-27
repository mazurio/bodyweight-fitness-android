package com.bodyweight.fitness.view.workout

import android.content.Context
import android.util.AttributeSet
import android.view.View

import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setInvisible
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences
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


        Stream.restTimerObservable
                .bindToLifecycle(view)
                .subscribe {
                    restoreView(view)
                }

        Stream.loggedSetRepsObservable
                .bindToLifecycle(view)
                .subscribe {
                    val view = (view as NavigationView)

                    showRestTimer(view)
                }

        Stream.loggedSecondsObservable
                .bindToLifecycle(view)
                .subscribe {
                    val view = (view as NavigationView)

                    showRestTimer(view)
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        val view = (view as NavigationView)

        val exercise = RoutineStream.exercise

        view.showTimerOrRepsLogger(exercise.isTimedSet)
        view.showPreviousNextButtons(exercise.isPrevious, exercise.isNext)
    }

    fun showRestTimer(view: NavigationView) {
        if (Preferences.showRestTimer) {
            val section = RoutineStream.exercise.section!!

            if (section.sectionId == "section0") {
                if (Preferences.showRestTimerAfterWarmup) {
                    view.showRestTimer()
                }
            } else if (section.sectionId == "section1") {
                if (Preferences.showRestTimerAfterBodylineDrills) {
                    view.showRestTimer()
                }
            } else {
                if (RoutineStream.routine.routineId != "routine0") {
                    if (Preferences.showRestTimerAfterFlexibilityExercises) {
                        view.showRestTimer()
                    }
                } else {
                    view.showRestTimer()
                }
            }
        }
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

    fun showRestTimer() {
        rest_timer_view.setVisible()
        timer_view.setGone()
        reps_logger_view.setGone()
    }

    fun showTimerOrRepsLogger(isTimed: Boolean) {
        if (!RestTimerShared.isPlaying) {
            if (isTimed) {
                rest_timer_view.setGone()
                timer_view.setVisible()
                reps_logger_view.setGone()
            } else {
                rest_timer_view.setGone()
                timer_view.setGone()
                reps_logger_view.setVisible()
            }
        } else {
            showRestTimer()
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