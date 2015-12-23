package io.mazur.fit.stream;

import io.mazur.fit.model.ActivityState;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ActivityStream {
    private static ActivityStream sInstance;

    private final PublishSubject<ActivityState> mActivitySubject = PublishSubject.create();

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

    public Observable<ActivityState> getActivityObservable() {
        return mActivitySubject.asObservable();
    }
}
