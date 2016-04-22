package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.adapter.CalendarListAdapter;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.stream.Stream;
import com.bodyweight.fitness.utils.DateUtils;

import org.joda.time.DateTime;

import java.util.Date;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.CalendarAdapter;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.stream.CalendarStream;
import com.bodyweight.fitness.view.CalendarView;

import io.realm.Realm;
import io.realm.RealmResults;

public class CalendarPresenter extends IPresenter<CalendarView> {
    private transient CalendarAdapter mCalendarAdapter;
    private transient CalendarListAdapter mCalendarListAdapter;

    @Override
    public void onCreateView(CalendarView view) {
        mView = view;

        mCalendarAdapter = new CalendarAdapter();
        mCalendarListAdapter = new CalendarListAdapter();

        mView.setAdapter(mCalendarAdapter);
        mView.setListAdapter(mCalendarListAdapter);
        mView.scrollToDefaultItem();

        onSubscribe();
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(Stream.INSTANCE
                .getDrawerObservable()
                .filter(id -> id.equals(R.id.action_menu_workout_log))
                .subscribe(menu -> {
                    mView.setAdapter(mCalendarAdapter);
                    mView.scrollToDefaultItem();
                }));

        subscribe(Stream.INSTANCE
                .getMenuObservable()
                .filter(id -> id.equals(R.id.action_today))
                .subscribe(id -> {
                    mView.scrollToDefaultItem();
                }));

        subscribe(CalendarStream.getInstance()
                .getCalendarDayChangedObservable()
                .subscribe(calendarDayChanged -> {
                    DateTime dateTime = DateUtils.getDate(
                            calendarDayChanged.presenterSelected,
                            calendarDayChanged.daySelected
                    );

                    if (isRoutineLogged(dateTime)) {
                        mView.showCardView();
                    } else {
                        mView.hideCardView();
                    }
                }));
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

        Realm realm = RepositoryStream.getInstance().getRealm();
        RealmResults<RepositoryRoutine> results = realm.where(RepositoryRoutine.class)
                .between("startTime", start, end)
                .findAll();

        if (!results.isEmpty()) {
            if (mCalendarListAdapter == null) {
                mCalendarListAdapter = new CalendarListAdapter();
            }

            mView.setListAdapter(mCalendarListAdapter);
            mCalendarListAdapter.setItems(results);

            return true;
        }

        return false;
    }

    public void onPageSelected(int position) {
        CalendarStream.getInstance().setCalendarPage(position);
    }
}
