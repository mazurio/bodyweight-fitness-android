package com.bodyweight.fitness.adapter;

import android.support.v4.view.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bodyweight.fitness.view.CalendarPagePresenter;
import com.bodyweight.fitness.view.CalendarPageView;
import com.bodyweight.fitness.view.widget.ViewPager;

import com.bodyweight.fitness.R;

public class CalendarAdapter extends PagerAdapter {
    public static final int DEFAULT_POSITION = 60;

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        CalendarPageView calendarItemView = (CalendarPageView) LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.view_calendar_page, viewGroup, false);

        CalendarPagePresenter calendarItemPresenter = (CalendarPagePresenter) calendarItemView.getMPresenter();
        calendarItemPresenter.setMViewPagerPosition(position);

        calendarItemView.updateView();

        viewPager.addView(calendarItemView);

        return calendarItemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return 61;
    }
}
