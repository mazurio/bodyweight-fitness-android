package com.bodyweight.fitness.stream;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ToolbarStream {
    private static ToolbarStream sInstance;

    private final PublishSubject<Integer> mMenuSubject = PublishSubject.create();

    private ToolbarStream() {}

    public static ToolbarStream getInstance() {
        if(sInstance == null) {
            sInstance = new ToolbarStream();
        }

        return sInstance;
    }

    public void setMenu(int menu) {
        mMenuSubject.onNext(menu);
    }

    public Observable<Integer> getMenuObservable() {
        return mMenuSubject.asObservable();
    }
}
