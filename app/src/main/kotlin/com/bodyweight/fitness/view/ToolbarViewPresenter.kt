package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.CalendarDay
import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_toolbar.view.*

import org.joda.time.DateTime

import java.util.*

object ToolbarPresenterState {
    var id: Int = R.id.action_menu_home
}

class ToolbarPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        getExerciseObservable()
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .bindToLifecycle(view)
                .filter { ToolbarPresenterState.id.equals(R.id.action_menu_home) }
                .subscribe {
                    setToolbarForHome(it)
                }

        Stream.calendarDayObservable
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .bindToLifecycle(view)
                .filter { ToolbarPresenterState.id.equals(R.id.action_menu_workout_log) }
                .subscribe {
                    setToolbarForWorkoutLog(it)
                }

        Stream.drawerObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter {
                    it.equals(R.id.action_menu_home) || it.equals(R.id.action_menu_workout_log)
                }
                .subscribe {
                    ToolbarPresenterState.id = it

                    setToolbar()
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setToolbar()
    }

    fun setToolbar() {
        when (ToolbarPresenterState.id) {
            R.id.action_menu_home ->
                setToolbarForHome(RoutineStream.exercise)
            R.id.action_menu_workout_log ->
                setToolbarForWorkoutLog(Stream.currentCalendarDay)
        }
    }

    fun setToolbarForHome(exercise: Exercise) {
        val view: ToolbarView = (mView as ToolbarView)

        view.inflateHomeMenu()

        view.setTitle(exercise.title)
        view.setSubtitle(exercise.section!!.title)
        view.setDescription(exercise.description)
    }

    fun setToolbarForWorkoutLog(calendarDay: CalendarDay?) {
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

    fun inflateHomeMenu() {
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.home)
    }

    fun inflateWorkoutLogMenu() {
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.calendar)
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