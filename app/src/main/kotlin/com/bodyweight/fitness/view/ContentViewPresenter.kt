package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.bodyweight.fitness.R

import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_main.view.*

object ContentShared {
    var id: Int = R.id.action_menu_home
}

class ContentPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter {
                    id -> id == R.id.action_menu_home ||
                        id == R.id.action_menu_change_routine ||
                        id == R.id.action_menu_workout_log
                }
                .subscribe { id ->
                    ContentShared.id = id

                    setContent(id)
                }

        RoutineStream.getInstance().routineChangedObservable.subscribe { routine ->
            ContentShared.id = R.id.action_menu_home

            (mView as ContentView).showHome()
        }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setContent(ContentShared.id)
    }

    fun setContent(id: Int) {
        val view: ContentView = (mView as ContentView)

        if (id == R.id.action_menu_home) {
            view.showHome()
        } else if (id == R.id.action_menu_workout_log) {
            view.showCalendar()
        }
    }
}

open class ContentView : AbstractView {
    override var mPresenter: AbstractPresenter = ContentPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {}

    fun showHome() {
        view_home.visibility = View.VISIBLE
        view_calendar.visibility = View.GONE
    }

    fun showCalendar() {
        view_home.visibility = View.GONE
        view_calendar.visibility = View.VISIBLE
    }
}