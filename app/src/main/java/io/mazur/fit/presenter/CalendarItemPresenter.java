package io.mazur.fit.presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashSet;

import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.model.ActivityState;
import io.mazur.fit.realm.RealmRoutine;
import io.mazur.fit.stream.ActivityStream;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.ui.ProgressActivity;
import io.mazur.fit.utils.Logger;
import io.mazur.fit.view.CalendarItemView;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class CalendarItemPresenter {
    private transient CalendarItemView mCalendarItemView;

    private int mViewPagerPosition = CalendarAdapter.DEFAULT_POSITION;
    private final PublishSubject<Integer> mDaySelectedSubject = PublishSubject.create();

    public CalendarItemPresenter(int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
    }

    public void onCreateView(CalendarItemView calendarItemView) {
        mCalendarItemView = calendarItemView;

        final DateTime dateTime = getDateBasedOnViewPagerPosition(mViewPagerPosition);

        int pointer = 0;
        for(int dayOfMonth = 1; dayOfMonth <= dateTime.dayOfMonth().getMaximumValue(); dayOfMonth++) {
            /**
             * Every 7 days we create new layout (new row) where we append day views.
             */
            if(pointer != 0 && pointer % 7 == 0) {
                calendarItemView.createRowLayout();
            }

            /**
             * Append empty days at the beginning of the month
             * if 1st of the month is not Monday.
             */
            if(dayOfMonth == 1) {
                pointer = pointer + dateTime.getDayOfWeek();

                for(int i = 1; i < dateTime.getDayOfWeek(); i++) {
                    DateTime previousMonthDays = dateTime.minusDays(dateTime.getDayOfWeek() - i);

                    calendarItemView.createDayView(previousMonthDays.getDayOfMonth(), false, false);
                }
            } else {
                pointer += 1;
            }

            if(isTodaysDate(dateTime, dayOfMonth)) {
                calendarItemView.createDayView(dayOfMonth, true, true);

                onDaySelected(dayOfMonth);
            } else {
                calendarItemView.createDayView(dayOfMonth, true, false);
            }

            /**
             * Append empty days at the end of the month.
             */
            if(dayOfMonth == dateTime.dayOfMonth().getMaximumValue()) {
                int newMonth = 1;
                for(int i = pointer; i < 42; i++) {
                    if(pointer % 7 == 0) {
                        calendarItemView.createRowLayout();
                    }

                    calendarItemView.createDayView(newMonth, false, false);

                    pointer += 1;
                    newMonth += 1;
                }
            }
        }
    }

    public boolean isTodaysDate(DateTime dateTime, int day) {
        DateTime today = new DateTime();

        return (today.getYear() == dateTime.getYear() &&
                today.getMonthOfYear() == dateTime.getMonthOfYear() &&
                today.getDayOfMonth() == day);
    }

    public int getTodaysDayOfTheMonth() {
        return new DateTime().getDayOfMonth();
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

    public DateTime getDateForDayOfMonth(int position, int dayOfMonth) {
        return getDateBasedOnViewPagerPosition(position)
                .withDayOfMonth(dayOfMonth)
                .withTimeAtStartOfDay();
    }

    /**
     * TODO: This should go to Realm for current month and fetch data. Then put it into a set.
     */
    public boolean isRoutineLogged(int dayOfMonth) {
        // Day of month at 00:00
        final Date start = getDateForDayOfMonth(mViewPagerPosition, dayOfMonth)
                .toDate();

        // Day of month at 23:59
        final Date end = getDateForDayOfMonth(mViewPagerPosition, dayOfMonth)
                .plusDays(1)
                .minusMinutes(1)
                .toDate();

        Realm realm = RealmStream.getInstance().getRealm();

        RealmRoutine routine = realm.where(RealmRoutine.class).between("date", start, end).findFirst();

        if(routine != null) {
            return true;
        }

        return false;
    }

    public HashSet<Integer> getThisMonthsHighlights(Context context, DateTime currentMonth) {
        HashSet<Integer> set = new HashSet<>();

        Realm realm = RealmStream.getInstance().getRealm();

        DateTime start = currentMonth
                .withDayOfMonth(1)
                .withTimeAtStartOfDay();

        DateTime end = currentMonth
                .withDayOfMonth(currentMonth.dayOfMonth().getMaximumValue())
                .withTimeAtStartOfDay()
                .plusDays(1);

        RealmResults<RealmRoutine> list = realm
                .where(RealmRoutine.class)
                .between("date", start.toDate(), end.toDate())
                .findAll();

        /**
         * Map each day to the set. We can then check if day exists in the set to change the date
         * in the calendar (e.g. highlight it).
         */
        for(RealmRoutine realmRoutine : list) {
            DateTime date = new DateTime(realmRoutine.getDate());

            set.add(date.getDayOfMonth());
        }

        return set;
    }


    public void onViewPagerPositionSelected(int position) {

    }

    public void onDaySelected(int dayOfMonth) {
        // Day of month at 00:00
        final Date start = getDateForDayOfMonth(mViewPagerPosition, dayOfMonth)
                .toDate();

        // Day of month at 23:59
        final Date end = getDateForDayOfMonth(mViewPagerPosition, dayOfMonth)
                .plusDays(1)
                .minusMinutes(1)
                .toDate();

        if(isRoutineLogged(dayOfMonth)) {
            mCalendarItemView.getCalendarActionButton().setVisibility(View.GONE);
            mCalendarItemView.getCalendarActionButton().setOnClickListener(v -> {
                // Do nothing.
            });

            mCalendarItemView.getCalendarDetails().setVisibility(View.VISIBLE);
            mCalendarItemView.getCalendarDetails().setOnClickListener(v -> {
                Intent intent = new Intent(mCalendarItemView.getContext(), ProgressActivity.class);

                intent.putExtra("exists", true);
                intent.putExtra("start", start);
                intent.putExtra("end", end);

                mCalendarItemView.getContext().startActivity(intent);
            });
        } else {
            mCalendarItemView.getCalendarActionButton().setVisibility(View.VISIBLE);
            mCalendarItemView.getCalendarActionButton().setOnClickListener(v -> {
                Intent intent = new Intent(mCalendarItemView.getContext(), ProgressActivity.class);

                intent.putExtra("exists", false);
                intent.putExtra("start", start);
                intent.putExtra("end", end);

                mCalendarItemView.getContext().startActivity(intent);
            });

            mCalendarItemView.getCalendarDetails().setVisibility(View.GONE);
            mCalendarItemView.getCalendarDetails().setOnClickListener(v -> {
                // Do nothing.
            });
        }

        mDaySelectedSubject.onNext(dayOfMonth);
    }

    public Observable<Integer> getDaySelectedObservable() {
        return mDaySelectedSubject.asObservable();
    }
}
