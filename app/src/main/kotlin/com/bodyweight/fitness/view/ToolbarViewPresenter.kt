package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R

import com.bodyweight.fitness.model.CalendarDayChanged
import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.stream.CalendarStream
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_toolbar.view.*

import org.joda.time.DateTime

import java.util.*

object ToolbarShared {
    var id: Int = R.id.action_menu_home
}

class ToolbarPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        getExerciseObservable()
                .bindToLifecycle(view)
                .filter { ToolbarShared.id.equals(R.id.action_menu_home) }
                .subscribe {
                    setToolbarForHome(it)
                }

        CalendarStream.getInstance()
                .calendarDayChangedObservable
                .bindToLifecycle(view)
                .filter { ToolbarShared.id.equals(R.id.action_menu_workout_log) }
                .subscribe {
                    setToolbarForWorkoutLog(it)
                }

        Stream.drawerObservable
                .bindToLifecycle(view)
                .subscribe {
                    ToolbarShared.id = it

                    setToolbar()
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setToolbar()
    }

    fun setToolbar() {
        when (ToolbarShared.id) {
            R.id.action_menu_home ->
                setToolbarForHome(RoutineStream.getInstance().exercise)
            R.id.action_menu_workout_log ->
                setToolbarForWorkoutLog(CalendarStream.getInstance().calendarDayChanged)
            R.id.action_menu_change_routine ->
                setToolbarForChangeRoutine()
        }
    }

    fun setToolbarForHome(exercise: Exercise) {
        val view: ToolbarView = (mView as ToolbarView)

        view.inflateHomeMenu()

        view.setTitle(exercise.title)
        view.setSubtitle(exercise.section.title)
        view.setDescription(exercise.description)
    }

    fun setToolbarForChangeRoutine() {
        val view: ToolbarView = (mView as ToolbarView)

        view.inflateChangeRoutineMenu()
        view.setSingleTitle("Change Routine")
    }

    fun setToolbarForWorkoutLog(calendarDayChanged: CalendarDayChanged?) {
        if (calendarDayChanged == null) {
            setDateTimeSingleTitle(DateTime())
        } else {
            setDateTimeSingleTitle(calendarDayChanged.date)
        }
    }

    private fun setDateTimeSingleTitle(dateTime: DateTime) {
        val view: ToolbarView = (mView as ToolbarView)

        view.inflateWorkoutLogMenu()
        view.setSingleTitle(
                dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH)
        )
    }
}

class ToolbarView : AbstractView {
    override var mPresenter: AbstractPresenter = ToolbarPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {}

    fun inflateHomeMenu() {
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.home)
    }

    fun inflateWorkoutLogMenu() {
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.calendar)
    }

    fun inflateChangeRoutineMenu() {
        toolbar.menu.clear()
    }

    fun setSingleTitle(text: String) {
        toolbar_layout.visibility = GONE
        toolbar.title = text
    }

    fun setTitle(text: String) {
        toolbar_layout.visibility = VISIBLE
        toolbar_exercise_title.text = text
    }

    fun setSubtitle(text: String) {
        toolbar_layout.visibility = VISIBLE
        toolbar_section_title.text = text
    }

    fun setDescription(text: String) {
        toolbar_layout.visibility = VISIBLE
        toolbar_exercise_description.text = text
    }
}