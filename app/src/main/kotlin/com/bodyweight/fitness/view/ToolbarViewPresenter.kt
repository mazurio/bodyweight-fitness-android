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

            R.id.action_menu_settings -> {
                setToolbarForSettings()
            }
        }
    }

    private fun setToolbarForHome() {
        val toolbarView: ToolbarView = (mView as ToolbarView)

        val routine = RoutineStream.routine

        toolbarView.setSpinner(routine.title, routine.subtitle)

        var isAdapterCreated = false
        val spinnerAdapter = ToolbarSpinnerAdapter()

        toolbarView.toolbar_spinner.adapter = spinnerAdapter
        toolbarView.toolbar_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (isAdapterCreated) {
                    val spinnerRoutine = spinnerAdapter.routines[pos]

                    RoutineStream.setRoutine(spinnerRoutine)

                    toolbarView.setSpinner(spinnerRoutine.title, spinnerRoutine.subtitle)

                    view?.let {
                        Toast.makeText(it.context,
                                "Switched routine to ${spinnerRoutine.title}",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    isAdapterCreated = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }
        }
    }

    private fun setToolbarForWorkoutLog(calendarDay: CalendarDay?) {
        if (calendarDay == null) {
            setDateTimeSingleTitle(DateTime())
        } else {
            setDateTimeSingleTitle(calendarDay.getDate())
        }
    }

    private fun setToolbarForSettings() {
        val view: ToolbarView = (mView as ToolbarView)
        view.setSingleTitle("Settings")
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

    fun setSpinner(title: String, subtitle: String) {
        toolbar_spinner_layout.setVisible()

        toolbar_spinner_title.text = title
        toolbar_spinner_subtitle.text = subtitle

        toolbar.title = ""
        toolbar.subtitle = ""
    }

    fun setSingleTitle(text: String) {
        toolbar_spinner_layout.setGone()

        toolbar.title = text
        toolbar.subtitle = ""
    }
}