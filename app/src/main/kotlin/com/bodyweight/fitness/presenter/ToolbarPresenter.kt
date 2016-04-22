package com.bodyweight.fitness.presenter

import com.bodyweight.fitness.R
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.CalendarDayChanged
import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.stream.CalendarStream
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.DateUtils
import com.bodyweight.fitness.view.AbstractView
import com.bodyweight.fitness.view.ToolbarView
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import org.joda.time.DateTime

import java.util.*

object Shared {
    var id: Int = R.id.action_menu_home
}

class ToolbarPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.getInstance()
                .exerciseObservable
                .bindToLifecycle(view)
                .filter { Shared.id.equals(R.id.action_menu_home) }
                .subscribe {
                    setToolbarForHome(it)
                }

        CalendarStream.getInstance()
                .calendarDayChangedObservable
                .bindToLifecycle(view)
                .filter { Shared.id.equals(R.id.action_menu_workout_log) }
                .subscribe {
                    setToolbarForWorkoutLog(it)
                }

        Stream.drawerObservable
                .bindToLifecycle(view)
                .subscribe {
                    Shared.id = it

                    setToolbar()
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setToolbar()
    }

    fun setToolbar() {
        when (Shared.id) {
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
            setDateTimeSingleTitle(DateUtils.getDate(
                    calendarDayChanged.presenterSelected,
                    calendarDayChanged.daySelected
            ))
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