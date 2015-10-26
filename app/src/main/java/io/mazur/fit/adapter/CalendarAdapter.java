package io.mazur.fit.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.mazur.fit.R;
import io.mazur.fit.view.CalendarItemView;

public class CalendarAdapter extends PagerAdapter {
    public static final int DEFAULT_POSITION = 60;

    private View mViewCalendarActionButton;
    private View mViewCalendarDetails;

    public CalendarAdapter(View viewCalendarActionButton, View viewCalendarDetails) {
        super();

        mViewCalendarActionButton = viewCalendarActionButton;
        mViewCalendarDetails = viewCalendarDetails;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        CalendarItemView calendarItemView = (CalendarItemView) LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.view_calendar_item, viewGroup, false);

        /**
         * We create presenter and view by manually calling the methods to pass the position
         * in the view pager.
         */
        calendarItemView.onCreate(position);
        calendarItemView.onCreateView();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                calendarItemView.getCalendarItemPresenter().onViewPagerPositionSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        calendarItemView.getCalendarItemPresenter().getDaySelectedObservable().subscribe((dayOfMonth) -> {
            if(calendarItemView.getCalendarItemPresenter().isRoutineLogged(dayOfMonth)) {
                mViewCalendarActionButton.setVisibility(View.GONE);
                mViewCalendarDetails.setVisibility(View.VISIBLE);
            } else {
                mViewCalendarActionButton.setVisibility(View.VISIBLE);
                mViewCalendarDetails.setVisibility(View.GONE);
            }
        });

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
        return 120;
    }
}
