package io.mazur.fit.presenter;

import org.joda.time.DateTime;

import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.view.CalendarView;

public class CalendarPresenter {
    private int mViewPagerPosition = CalendarAdapter.DEFAULT_POSITION;

    public CalendarPresenter(int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
    }

    public void onCreateView(CalendarView calendarView) {
        final DateTime dateTime = getDateBasedOnViewPagerPosition(mViewPagerPosition);

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

                    calendarView.createDayView(previousMonthDays.getDayOfMonth(), "#7D7D85", false);
                }
            } else {
                pointer++;
            }

            if(day == 10) {
                calendarView.createDayViewSelective(10);
            } else if(isTodaysDate(dateTime, day)) {
                calendarView.createDayViewActive(day);
            } else {
                calendarView.createDayView(day, "#FFFFFF", true);
            }

            /**
             * Every 7 days we create new layout (new row) where we append day views.
             */
            if(pointer % 7 == 0) {
                calendarView.createRowLayout();
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

    public void onPositionSelected(int position) {
        if(position == CalendarAdapter.DEFAULT_POSITION) {
            // highlight todaysDate
        } else if(position == mViewPagerPosition) {
            // highlight first day of the month
        }
    }
}
