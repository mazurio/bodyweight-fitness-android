package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_main.view.*

class ContentPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .subscribe {
                    setContent(it)
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setContent(Stream.currentDrawerId)
    }

    fun setContent(id: Int) {
        val view: ContentView = (mView as ContentView)

        when (id) {
            R.id.action_menu_home -> view.showHome()
            R.id.action_menu_workout_log -> view.showCalendar()
            R.id.action_menu_settings -> view.showSettings()
        }
    }
}

open class ContentView : AbstractView {
    override var presenter: AbstractPresenter = ContentPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun showHome() {
        view_home.setVisible()
        view_calendar.setGone()
        view_settings.setGone()
    }

    fun showCalendar() {
        view_home.setGone()
        view_calendar.setVisible()
        view_settings.setGone()
    }

    fun showSettings() {
        view_home.setGone()
        view_calendar.setGone()
        view_settings.setVisible()
    }
}