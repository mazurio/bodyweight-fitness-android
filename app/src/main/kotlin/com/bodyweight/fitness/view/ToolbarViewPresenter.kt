package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.model.CalendarDay
import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_toolbar.view.*

import org.joda.time.DateTime

import java.util.*

class ToolbarPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .filter { Stream.currentDrawerId.equals(R.id.action_menu_workout) }
                .subscribe {
                    setToolbarForWorkout(it)
                }

        Stream.calendarDayObservable()
                .bindToLifecycle(view)
                .filter { Stream.currentDrawerId.equals(R.id.action_menu_workout_log) }
                .subscribe {
                    setToolbarForWorkoutLog(it)
                }

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .filter {
                    it.equals(R.id.action_menu_home)
                            || it.equals(R.id.action_menu_workout)
                            || it.equals(R.id.action_menu_workout_log)
                }
                .subscribe {
                    setToolbar()
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setToolbar()
    }

    fun setToolbar() {
        when (Stream.currentDrawerId) {
            R.id.action_menu_home -> {
                setToolbarForHome()
            }
            R.id.action_menu_workout -> {
                setToolbarForWorkout(RoutineStream.exercise)
            }
            R.id.action_menu_workout_log -> {
                setToolbarForWorkoutLog(Stream.currentCalendarDay)
            }
        }
    }

    private fun setToolbarForHome() {
        val view: ToolbarView = (mView as ToolbarView)

        view.setTitleSubtitle("Bodyweight Fitness", "Home")
    }

    private fun setToolbarForWorkout(exercise: Exercise) {
        val view: ToolbarView = (mView as ToolbarView)

        view.setTitle(exercise.title)
        view.setSubtitle(exercise.section!!.title)
        view.setDescription(exercise.description)
    }

    private fun setToolbarForWorkoutLog(calendarDay: CalendarDay?) {
        if (calendarDay == null) {
            setDateTimeSingleTitle(DateTime())
        } else {
            setDateTimeSingleTitle(calendarDay.getDate())
        }
    }

    private fun setDateTimeSingleTitle(dateTime: DateTime) {
        val view: ToolbarView = (mView as ToolbarView)

        view.setSingleTitle(dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH))
    }
}

class ToolbarView : AbstractView {
    override var presenter: AbstractPresenter = ToolbarPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setSingleTitle(text: String) {
        toolbar_layout.setGone()
        toolbar.title = text
        toolbar.subtitle = ""
    }

    fun setTitleSubtitle(title: String, subtitle: String) {
        toolbar_layout.setGone()
        toolbar.title = title
        toolbar.subtitle = subtitle
    }

    fun setTitle(text: String) {
        toolbar_layout.setVisible()
        toolbar_exercise_title.text = text
    }

    fun setSubtitle(text: String) {
        toolbar_layout.setVisible()
        toolbar_section_title.text = text
    }

    fun setDescription(text: String) {
        toolbar_layout.setVisible()
        toolbar_exercise_description.text = text
    }
}