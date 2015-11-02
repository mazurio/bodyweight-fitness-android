package io.mazur.fit.presenter;

import android.widget.Toast;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.view.CalendarView;

public class CalendarPresenter {
    private transient CalendarView mCalendarView;

    private CalendarAdapter mCalendarAdapter;

    public void onCreateView(CalendarView calendarView) {
        mCalendarView = calendarView;

        mCalendarAdapter = new CalendarAdapter(
                mCalendarView.getViewPager(),
                mCalendarView.getViewCalendarActionButton(),
                mCalendarView.getViewCalendarDetails()
        );


        mCalendarView.getViewPager().setAdapter(mCalendarAdapter);
        mCalendarView.getViewPager().setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false);

        mCalendarView.getViewCalendarDetailsToolbar().inflateMenu(R.menu.calendar_card);
        mCalendarView.getViewCalendarDetailsToolbar().setOnMenuItemClickListener(item -> {
            switch(item.getItemId()) {
                case R.id.action_remove_logged_workout: {
                    Toast.makeText(mCalendarView.getContext(), "Deleted?", Toast.LENGTH_SHORT).show();

                    return true;
                }
            }

            return false;
        });
    }
}
