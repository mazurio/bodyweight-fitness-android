package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.extension.debug
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
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .bindToLifecycle(view)
                .filter { Stream.currentDrawerId.equals(R.id.action_menu_home) }
                .subscribe {
                    if (Stream.currentHomePage == 0) {
                        setToolbarForWelcome()
                    } else if (Stream.currentHomePage == 2) {
                        setToolbarForSummary()
                    } else {
                        setToolbarForHome(it)
                    }
                }

        Stream.calendarDayObservable()
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .bindToLifecycle(view)
                .filter { Stream.currentDrawerId.equals(R.id.action_menu_workout_log) }
                .subscribe {
                    setToolbarForWorkoutLog(it)
                }

        Stream.homePageObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter { Stream.currentDrawerId.equals(R.id.action_menu_home) }
                .subscribe {
                    if (it == 0) {
                        setToolbarForWelcome()
                    } else if (it == 2) {
                        setToolbarForSummary()
                    } else {
                        setToolbar()
                    }
                }

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter {
                    it.equals(R.id.action_menu_home) || it.equals(R.id.action_menu_workout_log)
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
            R.id.action_menu_home ->
                if (Stream.currentHomePage == 0) {
                    setToolbarForWelcome()
                } else if (Stream.currentHomePage == 2) {
                    setToolbarForSummary()
                } else {
                    setToolbarForHome(RoutineStream.exercise)
                }
            R.id.action_menu_workout_log ->
                setToolbarForWorkoutLog(Stream.currentCalendarDay)
        }
    }

    private fun setToolbarForWelcome() {
        val view: ToolbarView = (mView as ToolbarView)
        val routine = RoutineStream.routine

        view.invalidateMenu()
        view.setTitleSubtitle(title = routine.title, subtitle = routine.subtitle)
    }

    private fun setToolbarForSummary() {
        val view: ToolbarView = (mView as ToolbarView)

        view.invalidateMenu()
        view.setSingleTitle("Summary")
    }

    private fun setToolbarForHome(exercise: Exercise) {
        val view: ToolbarView = (mView as ToolbarView)

        view.inflateHomeMenu()

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

        view.inflateWorkoutLogMenu()
        view.setSingleTitle(dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH))
    }
}

class ToolbarView : AbstractView {
    override var mPresenter: AbstractPresenter = ToolbarPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {}

    fun invalidateMenu() {
        toolbar.menu.clear()
    }

    fun inflateHomeMenu() {
        invalidateMenu()

        toolbar.inflateMenu(R.menu.home)
    }

    fun inflateWorkoutLogMenu() {
        invalidateMenu()

        toolbar.inflateMenu(R.menu.calendar)
    }

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