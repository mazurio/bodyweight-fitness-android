package io.mazur.fit.presenter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.view.CalendarView;

public class CalendarPresenter {
    private int mViewPagerPosition = CalendarAdapter.DEFAULT_POSITION;

    public CalendarPresenter(int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
    }

    public void onCreateView(CalendarView calendarView) {
        final DateTime dateTime = getDateBasedOnViewPagerPosition(mViewPagerPosition);

        LinearLayout row = (LinearLayout) LayoutInflater
                .from(calendarView.getContext())
                .inflate(R.layout.view_calendar_item_row, calendarView.getLayout(), false);

        calendarView.getLayout().addView(row);

        int pointer = 0;
        for(int day = 1; day <= dateTime.dayOfMonth().getMaximumValue(); day++) {
            if(day == 1) {
                pointer = pointer + dateTime.getDayOfWeek();

                /**
                 * Append empty days at the beginning of the month
                 * if 1st of the month is not Monday.
                 */
                for(int i = 1; i < dateTime.getDayOfWeek(); i++) {
                    DateTime previousMonthDays = dateTime.minusDays(dateTime.getDayOfWeek() - i);

                    TextView dayTextView = (TextView) LayoutInflater
                            .from(calendarView.getContext())
                            .inflate(R.layout.view_calendar_item_day, row, false);

                    dayTextView.setTextColor(Color.parseColor("#7D7D85"));
                    dayTextView.setText(String.valueOf(previousMonthDays.getDayOfMonth()));

                    row.addView(dayTextView);
                }
            } else {
                pointer++;
            }

            TextView dayTextView = (TextView) LayoutInflater
                    .from(calendarView.getContext())
                    .inflate(R.layout.view_calendar_item_day, row, false);

            if(isTodaysDate(dateTime, day)) {
                dayTextView.setTextColor(Color.parseColor("#009688"));
            }

            dayTextView.setText(String.valueOf(day));

            row.addView(dayTextView);

            /**
             * Every 7 days we create new layout (new row) where we append day views.
             */
            if(pointer % 7 == 0) {
                row = (LinearLayout) LayoutInflater
                        .from(calendarView.getContext())
                        .inflate(R.layout.view_calendar_item_row, calendarView.getLayout(), false);

                calendarView.getLayout().addView(row);
            }

            /**
             * TODO: Needs to display days after the end of the month.
             */
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
}
