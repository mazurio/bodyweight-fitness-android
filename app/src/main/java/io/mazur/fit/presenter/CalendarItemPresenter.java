package io.mazur.fit.presenter;

import org.joda.time.DateTime;

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
        DateTime sunday = monday.plusDays(7);

        Logger.d("Month " + monday.toString("MMMM"));

        for(int i = 0; i < 7; i++) {
            DateTime dayOfWeek = monday.plusDays(i);

            mCalendarItemView.getDays()
                    .get(i)
                    .setText(dayOfWeek.dayOfMonth().getAsString());
        }
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
