package com.bodyweight.fitness.presenter;

import android.widget.TextView;

import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.utils.DateUtils;
import com.bodyweight.fitness.utils.ViewUtils;
import com.bodyweight.fitness.view.CalendarItemView;

import org.joda.time.DateTime;

import java.util.Date;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.CalendarAdapter;
import com.bodyweight.fitness.stream.CalendarStream;
import io.realm.Realm;

public class CalendarItemPresenter {
    private transient CalendarItemView mCalendarItemView;

    private int mViewPagerPosition = CalendarAdapter.DEFAULT_POSITION;

    private boolean mIsTodaysWeek = false;
    private int mTodaysDate = 3;

    public CalendarItemPresenter(int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
    }

    public void onCreateView(CalendarItemView calendarItemView) {
        mCalendarItemView = calendarItemView;

        DateTime monday = DateUtils.getDate(mViewPagerPosition);

        for(int i = 0; i < 7; i++) {
            DateTime dayOfWeek = monday.plusDays(i);

            TextView view = mCalendarItemView.getDays().get(i);

            if(isTodaysDate(dayOfWeek, dayOfWeek.getDayOfMonth())) {
                mIsTodaysWeek = true;
                mTodaysDate = i;

                view.setTag(true);

                ViewUtils.setBackgroundResourceWithPadding(
                        view, R.drawable.rounded_corner_active
                );

                /**
                 * If we create view when other is visible, we do not want to click on the
                 * current item.
                 */
                if(CalendarStream.getInstance().getCalendarPage() == mViewPagerPosition) {
                    mCalendarItemView.onDaysClick(view);
                }
            } else {
                view.setTag(false);
            }

            if(isRoutineLogged(dayOfWeek)) {
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dot);
            } else {
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dot_invisible);
            }

            view.setText(dayOfWeek.dayOfMonth().getAsString());
        }

        CalendarStream.getInstance().getCalendarPageObservable().subscribe(position -> {
            if(position == mViewPagerPosition) {
                if (mIsTodaysWeek) {
                    mCalendarItemView.selectDay(mTodaysDate);
                } else {
                    mCalendarItemView.selectDay(3);
                }
            }
        });

        CalendarStream.getInstance()
                .getCalendarDayChangedObservable()
                .subscribe(calendarDayChanged -> {
                    if(mViewPagerPosition != calendarDayChanged.presenterSelected) {
                        mCalendarItemView.unselectClickedDay();
                    }
                });
    }

    public int getViewPagerPosition() {
        return mViewPagerPosition;
    }

    public boolean isTodaysDate(DateTime dateTime, int day) {
        DateTime today = new DateTime();

        return (today.getYear() == dateTime.getYear() &&
                today.getMonthOfYear() == dateTime.getMonthOfYear() &&
                today.getDayOfMonth() == day);
    }

    public boolean isRoutineLogged(DateTime dateTime) {
        final Date start = dateTime
                .withTimeAtStartOfDay()
                .toDate();

        final Date end = dateTime
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toDate();

        Realm realm = RepositoryStream.getInstance().getRealm();
        RepositoryRoutine routine = realm.where(RepositoryRoutine.class)
                .between("startTime", start, end)
                .findFirst();

        return (routine != null);
    }
}
