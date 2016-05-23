package com.bodyweight.fitness.view

import android.content.Context
import android.support.design.widget.TabLayout
import android.util.AttributeSet

import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.RoutineStream
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_timer.view.*
import kotlinx.android.synthetic.main.view_workout.view.*

class WorkoutViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    (view as WorkoutView).showHideViewTabs(it.isTimedSet)
                }
    }
}

open class WorkoutView : AbstractView {
    override var mPresenter: AbstractPresenter = WorkoutViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        super.onCreateView()

        view_tabs.addTab(view_tabs.newTab().setText("Timer"))
        view_tabs.addTab(view_tabs.newTab().setText("Reps Logger"))

        view_tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                    val position = tab.position

                    if (position == 0) {
                        timer_view.setVisible()
                        reps_logger_view.setGone()
                    } else {
                        timer_view.setGone()
                        reps_logger_view.setVisible()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) { }
                override fun onTabReselected(tab: TabLayout.Tab) { }
            })

        if (RoutineStream.exercise.isTimedSet) {
            view_tabs.getTabAt(0)?.select()
        } else {
            view_tabs.getTabAt(1)?.select()
        }
    }

    fun showHideViewTabs(isTimed: Boolean) {
        if (isTimed) {
            if (view_tabs.tabCount == 2) {
                view_tabs.removeAllTabs()

                view_tabs.addTab(view_tabs.newTab().setText("Timer"))
            }

            view_tabs.getTabAt(0)?.select()
        } else {
            if (view_tabs.tabCount == 1) {
                view_tabs.removeAllTabs()

                view_tabs.addTab(view_tabs.newTab().setText("Timer"))
                view_tabs.addTab(view_tabs.newTab().setText("Reps Logger"))
            }

            view_tabs.getTabAt(1)?.select()
        }
    }
}