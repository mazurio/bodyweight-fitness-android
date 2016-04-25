package com.bodyweight.fitness.stream;

import com.bodyweight.fitness.model.CalendarDayChanged;

import rx.Observable;
import rx.subjects.PublishSubject;

public class CalendarStream {
    private static CalendarStream sInstance;

    private final PublishSubject<Integer> mCalendarPageSubject = PublishSubject.create();
    private final PublishSubject<CalendarDayChanged> mCalendarDayChangedSubject = PublishSubject.create();

    private int mCalendarPage;
    private CalendarDayChanged mCalendarDayChanged = new CalendarDayChanged();

    private CalendarStream() {}

    public static CalendarStream getInstance() {
        if(sInstance == null) {
            sInstance = new CalendarStream();
        }

        return sInstance;
    }

    public void setCalendarPage(int calendarPage) {
        mCalendarPage = calendarPage;
        mCalendarPageSubject.onNext(calendarPage);
    }

    public Observable<Integer> getCalendarPageObservable() {
        return mCalendarPageSubject.asObservable();
    }

    public int getCalendarPage() {
        return mCalendarPage;
    }

    public void setCalendarDay(CalendarDayChanged calendarDayChanged) {
        mCalendarDayChanged = calendarDayChanged;
        mCalendarDayChangedSubject.onNext(calendarDayChanged);
    }

    public Observable<CalendarDayChanged> getCalendarDayChangedObservable() {
        return mCalendarDayChangedSubject.asObservable();
    }

    public CalendarDayChanged getCalendarDayChanged() {
        return mCalendarDayChanged;
    }
}
