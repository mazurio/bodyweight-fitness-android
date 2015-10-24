package io.mazur.fit.presenter;

import android.widget.Toast;

import org.joda.time.DateTime;

import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.utils.Logger;
import io.mazur.fit.view.CalendarItemView;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class CalendarItemPresenter {
    private CalendarItemView mCalendarItemView;

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
            if(dayOfMonth == 1) {
                pointer = pointer + dateTime.getDayOfWeek();

                /**
                 * Append empty days at the beginning of the month
                 * if 1st of the month is not Monday.
                 */
                for(int i = 1; i < dateTime.getDayOfWeek(); i++) {
                    DateTime previousMonthDays = dateTime.minusDays(dateTime.getDayOfWeek() - i);

                    calendarItemView.createDayView(previousMonthDays.getDayOfMonth(), false, false);
                }
            } else {
                pointer++;
            }

            if(isTodaysDate(dateTime, dayOfMonth)) {
                calendarItemView.createDayView(dayOfMonth, true, true);

                onDaySelected(dayOfMonth);
            } else {
                calendarItemView.createDayView(dayOfMonth, true, false);
            }

            /**
             * Every 7 days we create new layout (new row) where we append day views.
             */
            if(pointer % 7 == 0) {
                calendarItemView.createRowLayout();
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

    /**
     * TODO: This should go to Realm for current month and fetch data. Then put it into a set.
     */
    public boolean isRoutineLogged(int dayOfMonth) {
        if(dayOfMonth == 10) {
            return true;
        }

        return false;
    }

    public void onViewPagerPositionSelected(int position) {

    }

    public void onDaySelected(int dayOfMonth) {
        mDaySelectedSubject.onNext(dayOfMonth);
    }

    public Observable<Integer> getDaySelectedObservable() {
        return mDaySelectedSubject.asObservable();
    }
}
