package io.mazur.fit.presenter;

import android.widget.TextView;

import org.joda.time.DateTime;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.utils.ViewUtils;
import io.mazur.fit.view.CalendarItemView;

public class CalendarItemPresenter {
    private transient CalendarItemView mCalendarItemView;

    private int mViewPagerPosition = CalendarAdapter.DEFAULT_POSITION;

    public CalendarItemPresenter(int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
    }

    public void onCreateView(CalendarItemView calendarItemView) {
        mCalendarItemView = calendarItemView;

        DateTime monday = getDateBasedOnViewPagerPosition(mViewPagerPosition);

        for(int i = 0; i < 7; i++) {
            DateTime dayOfWeek = monday.plusDays(i);

            TextView view = mCalendarItemView.getDays().get(i);

            if(isTodaysDate(dayOfWeek, dayOfWeek.getDayOfMonth())) {
                view.setTag(true);

                ViewUtils.setBackgroundResourceWithPadding(
                        view, R.drawable.rounded_corner_active
                );
            } else {
                view.setTag(false);
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

    public DateTime getDateBasedOnViewPagerPosition(int position) {
        if(position == CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .dayOfWeek()
                    .withMinimumValue();
        } else if (position < CalendarAdapter.DEFAULT_POSITION) {
            return new DateTime()
                    .minusWeeks(CalendarAdapter.DEFAULT_POSITION - position)
                    .dayOfWeek()
                    .withMinimumValue();
        } else {
            return new DateTime()
                    .plusWeeks(position - CalendarAdapter.DEFAULT_POSITION)
                    .dayOfWeek()
                    .withMinimumValue();
        }
    }
}
