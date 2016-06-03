package com.bodyweight.fitness.view

import android.content.Context
import android.support.design.widget.TabLayout
import android.util.AttributeSet

import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.stream.WorkoutViewType

import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_timer.view.*
import kotlinx.android.synthetic.main.view_workout.view.*

class WorkoutViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        val view = view as WorkoutView

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    view.showHideViewTabs(it.isTimedSet)
                }
    }
}

open class WorkoutView : AbstractView {
    override var presenter: AbstractPresenter = WorkoutViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        super.onCreateView()

        view_tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    "Timer" -> {
                        Stream.setWorkoutView(WorkoutViewType.Timer)

                        timer_view.setVisible()
                        reps_logger_view.setGone()
                        rest_timer_view.setGone()
                    }
                    "Reps Logger" -> {
                        Stream.setWorkoutView(WorkoutViewType.RepsLogger)

                        timer_view.setGone()
                        reps_logger_view.setVisible()
                        rest_timer_view.setGone()
                    }
                    else -> {
                        Stream.setWorkoutView(WorkoutViewType.RestTimer)

                        timer_view.setGone()
                        reps_logger_view.setGone()
                        rest_timer_view.setVisible()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })

        showHideViewTabs(RoutineStream.exercise.isTimedSet)
    }

    fun showHideViewTabs(isTimed: Boolean) {
        if (isTimed) {
            if (view_tabs.tabCount != 1 || view_tabs.getTabAt(0)!!.text != "Timer") {
                view_tabs.removeAllTabs()

                addTab("Timer")
            }

            selectFirstTab()

            Stream.setWorkoutView(WorkoutViewType.Timer)
        } else {
            if (view_tabs.tabCount != 2 || view_tabs.getTabAt(1)!!.text != "Reps Logger") {
                view_tabs.removeAllTabs()

                addTab("Timer")
                addTab("Reps Logger")
            }

            selectSecondTab()

            Stream.setWorkoutView(WorkoutViewType.RepsLogger)
        }
    }

    fun selectFirstTab() {
        view_tabs.getTabAt(0)?.select()
    }

    fun selectSecondTab() {
        view_tabs.getTabAt(1)?.select()
    }

    fun addTab(title: String) {
        view_tabs.addTab(view_tabs.newTab().setText(title))
    }
}