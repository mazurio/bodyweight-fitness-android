package io.mazur.fit.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mazur.fit.R;

public class CalendarAdapter extends PagerAdapter {
    public CalendarAdapter() {
        super();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ViewPager viewPager = (ViewPager) container;

        View inflatedView = LayoutInflater.from(container.getContext()).inflate(R.layout.view_calendar_item, container, false);
        viewPager.addView(inflatedView);

        return inflatedView;
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
        return 120;
    }
}
