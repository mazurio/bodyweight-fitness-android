package io.mazur.fit.stream;

import rx.Observable;
import rx.subjects.PublishSubject;

public class DrawerStream {
    private static DrawerStream sInstance;

    private final PublishSubject<Integer> mMenuSubject = PublishSubject.create();

    private DrawerStream() {}

    public static DrawerStream getInstance() {
        if(sInstance == null) {
            sInstance = new DrawerStream();
        }

        return sInstance;
    }

    public void setMenu(int id) {
        mMenuSubject.onNext(id);
    }

    public Observable<Integer> getMenuObservable() {
        return mMenuSubject.asObservable();
    }
}
