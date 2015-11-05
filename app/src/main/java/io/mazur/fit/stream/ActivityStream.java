package io.mazur.fit.stream;

import io.mazur.fit.model.ActivityPresenterState;
import io.mazur.fit.model.ActivityState;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ActivityStream {
    private static ActivityStream sInstance;

    private final PublishSubject<ActivityState> mActivitySubject = PublishSubject.create();
    private final PublishSubject<ActivityPresenterState> mActivityPresenterSubject = PublishSubject.create();
    private final PublishSubject<Integer> mMenuSubject = PublishSubject.create();

    private ActivityStream() {}

    public static ActivityStream getInstance() {
        if(sInstance == null) {
            sInstance = new ActivityStream();
        }

        return sInstance;
    }

    public void setActivityState(ActivityState activityState) {
        mActivitySubject.onNext(activityState);
    }

    public void setActivityPresenterState(ActivityPresenterState activityPresenterState) {
        mActivityPresenterSubject.onNext(activityPresenterState);
    }

    public void setMenuState(int menuState) {
        mMenuSubject.onNext(menuState);
    }

    public Observable<ActivityState> getActivityObservable() {
        return mActivitySubject.asObservable();
    }

    public Observable<ActivityPresenterState> getActivityPresenterObservable() {
        return mActivityPresenterSubject.asObservable();
    }

    public Observable<Integer> getMenuObservable() {
        return mMenuSubject.asObservable();
    }
}
