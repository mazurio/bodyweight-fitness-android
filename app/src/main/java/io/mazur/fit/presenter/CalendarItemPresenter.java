package io.mazur.fit.presenter;

import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Date;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.utils.DateUtils;
import io.mazur.fit.utils.ViewUtils;
import io.mazur.fit.view.CalendarItemView;
import io.realm.Realm;

public class CalendarItemPresenter {
    private transient CalendarItemView mCalendarItemView;

    private int mViewPagerPosition = CalendarAdapter.DEFAULT_POSITION;

    private boolean mIsTodaysWeek = false;
    private int mTodaysDate = 3;

    public CalendarItemPresenter(int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
    }

    public void onCreateView(CalendarItemView calendarItemView, int currentViewPagerPosition) {
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
                if(currentViewPagerPosition == mViewPagerPosition) {
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
    }

    public int getViewPagerPosition() {
        return mViewPagerPosition;
    }

    /**
     * Called by Adapter
     *
     * @param position of the selected page.
     */
    public void onPageSelected(int position) {
        if(position == mViewPagerPosition) {
            if (mIsTodaysWeek) {
                mCalendarItemView.selectDay(mTodaysDate);
            } else {
                mCalendarItemView.selectDay(3);
            }
        }
    }

    public void onDaySelected(CalendarDayChanged calendarDayChanged) {
        if(mViewPagerPosition != calendarDayChanged.presenterSelected) {
            mCalendarItemView.unselectClickedDay();
        }
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

        Realm realm = RealmStream.getInstance().getRealm();
        RealmRoutine routine = realm.where(RealmRoutine.class)
                .between("startTime", start, end)
                .findFirst();

        if(routine != null) {
            return true;
        }

        return false;
    }
}
