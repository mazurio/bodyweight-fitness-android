package com.bodyweight.fitness.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView

import com.bodyweight.fitness.R
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.isRoutineLogged
import com.bodyweight.fitness.isToday
import com.bodyweight.fitness.model.CalendarDay
import com.bodyweight.fitness.setBackgroundResourceWithPadding
import com.bodyweight.fitness.stream.Stream
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_calendar_page.view.*

class CalendarPagePresenter : AbstractPresenter() {
    var viewPagerPosition = 0
    var isTodaysWeek = false
    var isTodaysDate = 3

    override fun updateView() {
        super.updateView()

        val view = (mView as CalendarPageView)

        val firstDayOfTheWeek = CalendarDay(viewPagerPosition, 0).getDate()

        for (index in 0..6) {
            val currentDayOfTheWeek = firstDayOfTheWeek.plusDays(index)
            if (currentDayOfTheWeek.isToday()) {
                isTodaysWeek = true
                isTodaysDate = index

                view.setActive(index)

                if (Stream.currentCalendarPage == viewPagerPosition) {
                    view.select(index)
                    clickedAt(index)
                }
            }

            view.setListener(index)
            view.setIsToday(index, currentDayOfTheWeek.isToday())
            view.showDot(index, currentDayOfTheWeek.isRoutineLogged())
            view.setText(index, currentDayOfTheWeek.dayOfMonth().asText)
        }

        Stream.calendarPageObservable()
                .bindToLifecycle(view)
                .filter { it == viewPagerPosition }
                .subscribe {
                    if (isTodaysWeek) {
                        view.select(isTodaysDate)
                        clickedAt(isTodaysDate)
                    } else {
                        view.select(3)
                        clickedAt(3)
                    }
                }

        Stream.calendarDayObservable()
                .bindToLifecycle(view)
                .subscribe {
                    if (it.page != viewPagerPosition) {
                        view.unselect(isTodaysDate)
                    } else {
                        view.select(it.day)
                    }
                }
    }

    fun clickedAt(dayView: Int) {
        Stream.setCalendarDay(CalendarDay(viewPagerPosition, dayView))
    }
}

open class CalendarPageView : AbstractView {
    override var mPresenter: AbstractPresenter = CalendarPagePresenter()

    var mClickedDay: Int = 3

    val mDayViews: List<TextView> by lazy {
        listOf(this.day_1, this.day_2, this.day_3, this.day_4, this.day_5, this.day_6, this.day_7)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() { }

    override fun updateView() {
        super.updateView()
    }

    fun setListener(dayView: Int) {
        val view: TextView? = mDayViews[dayView]

        view?.setOnClickListener {
            select(dayView)

            (mPresenter as CalendarPagePresenter).clickedAt(dayView)
        }
    }

    fun select(dayView: Int) {
        val view: TextView? = mDayViews[dayView]

        unselect(mClickedDay)

        view?.let {
            it.setTextColor(Color.parseColor("#ffffff"))
            it.setBackgroundResourceWithPadding(R.drawable.rounded_corner_today)
        }

        mClickedDay = dayView
    }

    fun unselect(dayView: Int) {
        val view: TextView? = mDayViews[dayView]
        val isToday = view?.tag as? Boolean ?: false

        val clickedView: TextView? = mDayViews[mClickedDay]

        clickedView?.let {
            if (isToday) {
                it.setTextColor(Color.parseColor("#00453E"))
                it.setBackgroundResourceWithPadding(R.drawable.rounded_corner_active)
            } else {
                it.setTextColor(Color.parseColor("#00453E"))
                it.setBackgroundResource(0)
            }
        }
    }

    fun setActive(dayView: Int) {
        val view: TextView = mDayViews[dayView]

        view.setBackgroundResourceWithPadding(R.drawable.rounded_corner_active)
    }

    fun setIsToday(dayView: Int, tag: Boolean) {
        val view: TextView = mDayViews[dayView]

        view.tag = tag
    }

    fun showDot(dayView: Int, show: Boolean) {
        val view: TextView = mDayViews[dayView]

        if (show) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dot)
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dot_invisible)
        }
    }

    fun setText(dayView: Int, text: String) {
        val view: TextView = mDayViews[dayView]

        view.text = text
    }
}