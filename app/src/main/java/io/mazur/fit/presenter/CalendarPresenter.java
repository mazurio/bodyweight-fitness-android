package io.mazur.fit.presenter;

import android.content.Intent;
import android.view.View;

import org.joda.time.DateTime;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.stream.ActivityStream;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.ui.ProgressActivity;
import io.mazur.fit.utils.DateUtils;
import io.mazur.fit.view.CalendarView;

import io.realm.Realm;

public class CalendarPresenter {
    private transient CalendarView mCalendarView;

    private CalendarAdapter mCalendarAdapter;
    private CalendarDayChanged mCalendarDayChanged;

    public void onCreateView(CalendarView calendarView) {
        mCalendarView = calendarView;

        mCalendarAdapter = new CalendarAdapter(
                mCalendarView.getViewPager()
        );

        mCalendarView.getViewPager().setAdapter(mCalendarAdapter);
        mCalendarView.getViewPager().setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false);

        mCalendarAdapter.getOnDaySelectedObservable().subscribe(calendarDayChanged -> {
            mCalendarDayChanged = calendarDayChanged;

            DateTime dateTime = DateUtils.getDate(
                    calendarDayChanged.presenterSelected,
                    calendarDayChanged.daySelected
            );

            mCalendarView.getDate().setText(dateTime.toString("EEEE, d MMMM"));

            if (isRoutineLogged(dateTime)) {
                mCalendarView.getDate().setVisibility(View.VISIBLE);
                mCalendarView.getCardView().setVisibility(View.VISIBLE);
                mCalendarView.getMessage().setVisibility(View.GONE);

                final DateTime start = dateTime.withTimeAtStartOfDay();
                final DateTime end = start.plusDays(1).minusMinutes(1);

                mCalendarView.getViewButton().setOnClickListener(view -> {
                    Intent intent = new Intent(mCalendarView.getContext(), ProgressActivity.class);

                    intent.putExtra("exists", true);
                    intent.putExtra("start", start.toDate());
                    intent.putExtra("end", end.toDate());

                    mCalendarView.getContext().startActivity(intent);
                });
            } else {
                mCalendarView.getDate().setVisibility(View.GONE);
                mCalendarView.getCardView().setVisibility(View.GONE);
                mCalendarView.getMessage().setVisibility(View.VISIBLE);

                mCalendarView.getViewButton().setOnClickListener(view -> {});
            }
        });

        ActivityStream.getInstance().getMenuObservable().subscribe(id -> {
            if(id == R.id.action_today) {
                mCalendarView.getViewPager().setCurrentItem(
                        CalendarAdapter.DEFAULT_POSITION, true
                );
            }
        });
    }

    public CalendarAdapter getCalendarAdapter() {
        return mCalendarAdapter;
    }

    public CalendarDayChanged getCalendarDayChanged() {
        return mCalendarDayChanged;
    }

    /**
     * Compare dates between 00:00 and 23:59.
     *
     * TODO: Asynchronous.
     * TODO: Should return Set of days.
     */
    public boolean isRoutineLogged(DateTime dateTime) {
        final DateTime start = dateTime.withTimeAtStartOfDay();
        final DateTime end = start.plusDays(1).minusMinutes(1);

        Realm realm = RealmStream.getInstance().getRealm();
        RealmRoutine routine = realm.where(RealmRoutine.class)
                .between("date", start.toDate(), end.toDate())
                .findFirst();

        return (routine != null);
    }
}
