package io.mazur.fit.presenter;

import org.joda.time.DateTime;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.utils.Logger;
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

        Logger.d("Month " + monday.toString("MMMM"));

        for(int i = 0; i < 7; i++) {
            DateTime dayOfWeek = monday.plusDays(i);

            // Highlight todays date
            if(isTodaysDate(dayOfWeek, dayOfWeek.getDayOfMonth())) {
                mCalendarItemView.getDays().get(i).setBackgroundDrawable(
                        mCalendarItemView.getContext().getResources().getDrawable(
                                R.drawable.rounded_corner_today
                        )
                );
            }

            mCalendarItemView.getDays()
                    .get(i)
                    .setText(dayOfWeek.dayOfMonth().getAsString());
        }
    }

    public void onViewPagerPositionChanged(int position) {
        Logger.d("position " + position);

        if(position == mViewPagerPosition) {
            // select first day!!!!
//            mCalendarItemView.getDays().get(0).setBackgroundDrawable(
//                    mCalendarItemView.getContext().getResources().getDrawable(
//                            R.drawable.rounded_corner_active
//                    )
//            );
        } else {
            // unselect any day...
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
