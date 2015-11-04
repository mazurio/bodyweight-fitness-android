package io.mazur.fit.presenter;

import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.view.CalendarView;

public class CalendarPresenter {
    private transient CalendarView mCalendarView;

    private CalendarAdapter mCalendarAdapter;

    public void onCreateView(CalendarView calendarView) {
        mCalendarView = calendarView;

        mCalendarAdapter = new CalendarAdapter(
                mCalendarView.getViewPager()
        );

        mCalendarView.getViewPager().setAdapter(mCalendarAdapter);
        mCalendarView.getViewPager().setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false);

        mCalendarAdapter.getOnDaySelectedObservable().subscribe(calendarDayChanged -> {
            mCalendarView.getRoutineTitle().setText(
                    calendarDayChanged.presenterSelected + ":" + calendarDayChanged.daySelected
            );
        });
    }
}
