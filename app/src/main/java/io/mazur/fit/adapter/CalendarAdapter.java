package io.mazur.fit.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.joda.time.DateTime;

import io.mazur.fit.R;
import io.mazur.fit.view.CalendarItemView;

public class CalendarAdapter extends PagerAdapter {
    public static final int DEFAULT_POSITION = 60;

    private ViewPager mViewCalendarPager;
    private View mViewCalendarActionButton;
    private View mViewCalendarDetails;

    private ActionBar mActionBar;

    private String mCurrentTitle;

    private int mCurrentPosition = DEFAULT_POSITION;
    private int mCurrentDayOfMonth = 1;

    public CalendarAdapter(ViewPager viewCalendarPager, View viewCalendarActionButton, View viewCalendarDetails, ActionBar actionBar) {
        super();

        mViewCalendarPager = viewCalendarPager;
        mViewCalendarActionButton = viewCalendarActionButton;
        mViewCalendarDetails = viewCalendarDetails;
        mActionBar = actionBar;

        mCurrentTitle = getMonthForActionBarTitle(DEFAULT_POSITION);
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        CalendarItemView calendarItemView = (CalendarItemView) LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.view_calendar_item, viewGroup, false);

        calendarItemView.onCreate(position);
        calendarItemView.onCreateView(mViewCalendarActionButton, mViewCalendarDetails);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;

                calendarItemView.getCalendarItemPresenter().onViewPagerPositionSelected(position);
                calendarItemView.getCalendarItemPresenter().getDaySelectedObservable().subscribe(dayOfMonth -> {
                    mCurrentDayOfMonth = dayOfMonth;
                });

                mCurrentTitle = getMonthForActionBarTitle(position);

                mActionBar.setTitle(mCurrentTitle);
                mActionBar.setSubtitle("");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        int position = mCurrentPosition;
        int dayOfMonth = mCurrentDayOfMonth;

        super.notifyDataSetChanged();

        mViewCalendarPager.setCurrentItem(position, false);

        Toast.makeText(mViewCalendarPager.getContext(), "Day of month: " + dayOfMonth, Toast.LENGTH_SHORT).show();
    }

    public void setToday() {
        // TODO: shouldSetDateToToday
    }

    public String getCurrentTitle() {
        return mCurrentTitle;
    }

    public DateTime getDateBasedOnViewPagerPosition(int position) {
        if(position == CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .dayOfMonth()
                    .withMinimumValue();
        } else if (position < CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .minusMonths(CalendarAdapter.DEFAULT_POSITION - position)
                    .dayOfMonth()
                    .withMinimumValue();
        } else {
            return new DateTime()
                    .plusMonths(position - CalendarAdapter.DEFAULT_POSITION)
                    .dayOfMonth()
                    .withMinimumValue();
        }
    }

    public String getMonthForActionBarTitle(int position) {
        DateTime today = new DateTime();
        DateTime dateTime = getDateBasedOnViewPagerPosition(position);

        if(dateTime.getYear() != today.getYear()) {
            return dateTime.toString("MMMM YYYY");
        } else {
            return dateTime.toString("MMMM");
        }
    }
}
