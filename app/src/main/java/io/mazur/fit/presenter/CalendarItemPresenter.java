package io.mazur.fit.presenter;

import android.widget.TextView;

import org.joda.time.DateTime;

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
                view.setTag(true);

                ViewUtils.setBackgroundResourceWithPadding(
                        view, R.drawable.rounded_corner_active
                );

                mCalendarItemView.onDaysClick(view);
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

    public void onPageSelected(int position) {
        /**
         * I don't like how it jumps.
         */
//        if(position == mViewPagerPosition) {
//            mCalendarItemView.selectFirstDay();
//        }
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
        final DateTime start = dateTime.withTimeAtStartOfDay();
        final DateTime end = start.plusDays(1).minusMinutes(1);

        Realm realm = RealmStream.getInstance().getRealm();
        RealmRoutine routine = realm.where(RealmRoutine.class)
                .between("date", start.toDate(), end.toDate())
                .findFirst();

        if(routine != null) {
            return true;
        }

        return false;
    }
}
