package com.bodyweight.fitness.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View

import com.bodyweight.fitness.R
import com.bodyweight.fitness.adapter.CalendarAdapter
import com.bodyweight.fitness.adapter.CalendarListAdapter
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.repository.RepositoryRoutine
import com.bodyweight.fitness.stream.CalendarStream
import com.bodyweight.fitness.stream.RepositoryStream
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_calendar.view.*
import org.joda.time.DateTime

class CalendarPresenter : AbstractPresenter() {
    @Transient
    val mCalendarAdapter = CalendarAdapter()

    @Transient
    val mCalendarListAdapter = CalendarListAdapter()

    override fun bindView(view: AbstractView) {
        super.bindView(view)

        val view = (view as CalendarView)

        view.view_calendar_pager.adapter = mCalendarAdapter
        view.view_calendar_list.adapter = mCalendarListAdapter

        view.scrollToDefaultItem()

        Stream.drawerObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter { id -> id == R.id.action_menu_workout_log }
                .subscribe {
                    view.scrollToDefaultItem()
                }

        Stream.menuObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter { it == R.id.action_today }
                .subscribe {
                    view.scrollToDefaultItem()
                }

        CalendarStream.getInstance()
                .calendarDayChangedObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    val dateTime = it.date

                    if (isRoutineLogged(dateTime)) {
                        view.showCardView()
                    } else {
                        view.hideCardView()
                    }
                }
    }

    fun isRoutineLogged(dateTime: DateTime): Boolean {
        val start = dateTime.withTimeAtStartOfDay().toDate()
        val end = dateTime.withTimeAtStartOfDay().plusDays(1).minusSeconds(1).toDate()

        val realm = RepositoryStream.getInstance().realm
        val results = realm.where(RepositoryRoutine::class.java)
                .between("startTime", start, end)
                .findAll()

        if (!results.isEmpty()) {
            mCalendarListAdapter.setItems(results)

            return true
        }

        return false
    }

    fun onPageSelected(position: Int) {
        CalendarStream.getInstance().calendarPage = position
    }
}

open class CalendarView : AbstractView {
    override var mPresenter: AbstractPresenter = CalendarPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        view_calendar_list.layoutManager = LinearLayoutManager(context)

        view_calendar_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val presenter = (mPresenter as CalendarPresenter)

                presenter.onPageSelected(position)
            }
        })
    }

    fun hideCardView() {
        view_calendar_list.visibility = View.GONE
        view_calendar_message.visibility = View.VISIBLE
    }

    fun showCardView() {
        view_calendar_list.visibility = View.VISIBLE
        view_calendar_message.visibility = View.GONE
    }

    fun scrollToDefaultItem() {
        view_calendar_pager.setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false)
    }
}