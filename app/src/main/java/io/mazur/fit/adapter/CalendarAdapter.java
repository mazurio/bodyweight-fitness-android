package io.mazur.fit.adapter;

import android.support.v4.view.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.WeakHashMap;

import io.mazur.fit.R;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.utils.Logger;
import io.mazur.fit.view.CalendarItemView;
import io.mazur.fit.view.widget.ViewPager;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class CalendarAdapter extends PagerAdapter {
    public static final int DEFAULT_POSITION = 60;

    private int mCurrentViewPagerPosition = DEFAULT_POSITION;

    private final PublishSubject<CalendarDayChanged> mOnDaySelectedSubject = PublishSubject.create();
    private final PublishSubject<Integer> mOnPageSelectedSubject = PublishSubject.create();

    private WeakHashMap<CalendarItemView, Subscription> mPageSubscriptionMap = new WeakHashMap<>();
    private WeakHashMap<CalendarItemView, Subscription> mDaySubscriptionMap = new WeakHashMap<>();

    public CalendarAdapter(ViewPager viewCalendarPager) {
        super();

        viewCalendarPager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerPosition = position;

                mOnPageSelectedSubject.onNext(position);
            }

            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        CalendarItemView calendarItemView = (CalendarItemView) LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.view_calendar_item, viewGroup, false);

        calendarItemView.onCreate(position, mOnDaySelectedSubject);
        calendarItemView.onCreateView(mCurrentViewPagerPosition);

        Subscription pageSubscription = mOnPageSelectedSubject.subscribe(
                calendarItemView.getCalendarItemPresenter()::onPageSelected
        );

        Subscription daySubscription = mOnDaySelectedSubject.subscribe(
                calendarItemView.getCalendarItemPresenter()::onDaySelected
        );

        mPageSubscriptionMap.put(calendarItemView, pageSubscription);
        mDaySubscriptionMap.put(calendarItemView, daySubscription);

        viewPager.addView(calendarItemView);

        return calendarItemView;
    }

    /**
     * Remove subscriptions made by item views to the day/page selected observables.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        CalendarItemView calendarItemView = (CalendarItemView) object;

        mPageSubscriptionMap.get(calendarItemView).unsubscribe();
        mDaySubscriptionMap.get(calendarItemView).unsubscribe();

        mPageSubscriptionMap.remove(calendarItemView);
        mDaySubscriptionMap.remove(calendarItemView);

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

    public Observable<CalendarDayChanged> getOnDaySelectedObservable() {
        return mOnDaySelectedSubject.asObservable();
    }
}
