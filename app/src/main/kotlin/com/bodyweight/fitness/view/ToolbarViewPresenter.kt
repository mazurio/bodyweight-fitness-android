package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Toast

import com.bodyweight.fitness.R
import com.bodyweight.fitness.adapter.ToolbarSpinnerAdapter
import com.bodyweight.fitness.model.CalendarDay
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

        Stream.calendarDayObservable()
                .bindToLifecycle(view)
                .filter { Stream.currentDrawerId.equals(R.id.action_menu_workout_log) }
                .subscribe {
                    setToolbarForWorkoutLog(it)
                }

        Stream.drawerObservable()
                .bindToLifecycle(view)
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

            R.id.action_menu_workout_log -> {
                setToolbarForWorkoutLog(Stream.currentCalendarDay)
            }
        }
    }

    private fun setToolbarForHome() {
        val toolbarView: ToolbarView = (mView as ToolbarView)

        val routine = RoutineStream.routine

        toolbarView.toolbar.title = routine.title
        toolbarView.toolbar.subtitle = routine.subtitle
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
        toolbar.title = text
        toolbar.subtitle = ""
    }
}