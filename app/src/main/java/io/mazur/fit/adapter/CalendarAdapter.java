package io.mazur.fit.adapter;

import android.support.v4.view.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mazur.fit.R;
import io.mazur.fit.view.CalendarItemView;
import io.mazur.fit.view.widget.ViewPager;

public class CalendarAdapter extends PagerAdapter {
    public static final int DEFAULT_POSITION = 60;

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        CalendarItemView calendarItemView = (CalendarItemView) LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.view_calendar_item, viewGroup, false);

        calendarItemView.onCreate(position);
        calendarItemView.onCreateView();

        viewPager.addView(calendarItemView);

        return calendarItemView;
    }

    /**
     * Remove subscriptions made by item views to the day/page selected observables.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        CalendarItemView calendarItemView = (CalendarItemView) object;

        calendarItemView.onDestroyView();

        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView(calendarItemView);
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
