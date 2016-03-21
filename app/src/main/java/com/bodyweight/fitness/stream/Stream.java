package com.bodyweight.fitness.stream;

import rx.Observable;
import rx.subjects.PublishSubject;

public class Stream {
    private static Stream sInstance;

    private final PublishSubject<Integer> mLoggedSecondsSubject = PublishSubject.create();
    private final PublishSubject<Integer> mMenuSubject = PublishSubject.create();

    private Stream() {}

    public static Stream getInstance() {
        if(sInstance == null) {
            sInstance = new Stream();
        }

        return sInstance;
    }

    public void setLoggedSeconds(int loggedSeconds) {
        mLoggedSecondsSubject.onNext(loggedSeconds);
    }

    public void setMenu(int menu) {
        mMenuSubject.onNext(menu);
    }

    public Observable<Integer> getLoggedSecondsObservable() {
        return mLoggedSecondsSubject.asObservable();
    }

    public Observable<Integer> getMenuObservable() {
        return mMenuSubject.asObservable();
    }
}
