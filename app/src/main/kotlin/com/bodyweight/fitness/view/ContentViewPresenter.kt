package com.bodyweight.fitness.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import com.bodyweight.fitness.R
import com.bodyweight.fitness.adapter.HomePagerAdapter

import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_main.view.*

class ContentPresenter : AbstractPresenter() {
    @Transient
    val homePagerAdapter = HomePagerAdapter()

    override fun bindView(view: AbstractView) {
        super.bindView(view)

        val view = view as ContentView

        view.setAdapter(homePagerAdapter)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    homePagerAdapter.set(it.isTimedSet)
                }

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter {
                    it.equals(R.id.action_menu_home) || it.equals(R.id.action_menu_workout_log)
                }
                .subscribe { setContent(it) }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setContent(Stream.currentDrawerId)
    }

    fun setContent(id: Int) {
        val view: ContentView = (mView as ContentView)

        if (id == R.id.action_menu_home) {
            view.showHome()
        } else if (id == R.id.action_menu_workout_log) {
            view.showCalendar()
        }
    }

    fun onPageSelected(position: Int) {
        Stream.setHomePage(position)
    }
}

open class ContentView : AbstractView {
    override var mPresenter: AbstractPresenter = ContentPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        view_home.offscreenPageLimit = 4
        view_home.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                (mPresenter as ContentPresenter).onPageSelected(position)
            }
        })
    }

    fun setAdapter(adapter: HomePagerAdapter) {
        view_home.adapter = adapter
    }

    fun showHome() {
        view_home.visibility = View.VISIBLE
        view_calendar.visibility = View.GONE
    }

    fun showCalendar() {
        view_home.visibility = View.GONE
        view_calendar.visibility = View.VISIBLE
    }
}