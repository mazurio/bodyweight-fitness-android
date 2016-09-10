package com.bodyweight.fitness.adapter

import android.support.v4.view.PagerAdapter

import android.view.View
import android.view.ViewGroup

import com.bodyweight.fitness.view.CalendarPagePresenter
import com.bodyweight.fitness.view.CalendarPageView
import com.bodyweight.fitness.view.widget.ViewPager

import com.bodyweight.fitness.R
import com.bodyweight.fitness.inflate

class CalendarPagerAdapter : PagerAdapter() {
    companion object {
        val DEFAULT_POSITION = 60
    }

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any {
        val viewPager = viewGroup as ViewPager

        val calendarPageView = viewGroup.inflate(R.layout.view_calendar_page) as CalendarPageView
        val calendarPagePresenter = calendarPageView.presenter as CalendarPagePresenter

        calendarPagePresenter.viewPagerPosition = position
        calendarPageView.updateView()

        viewPager.addView(calendarPageView)

        return calendarPageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return DEFAULT_POSITION + 1
    }
}
