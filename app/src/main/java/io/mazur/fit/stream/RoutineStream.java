package io.mazur.fit.stream;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import io.mazur.fit.App;
import io.mazur.fit.Constants;
import io.mazur.fit.R;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.JSONRoutine;
import io.mazur.fit.utils.Logger;

import io.mazur.glacier.Duration;
import io.mazur.glacier.Glacier;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class RoutineStream {
    private static RoutineStream sInstance;

    private Routine mRoutine;
    private Exercise mExercise;

    private final PublishSubject<Routine> mRoutineSubject = PublishSubject.create();
    private final PublishSubject<Exercise> mExerciseSubject = PublishSubject.create();
    private final PublishSubject<Routine> mLevelChangedSubject = PublishSubject.create();

    private RoutineStream() {
        /**
         * We should save levels only, not the whole object
         * as we will never get any updates when JSON is udpated.
         */
        try {
            JSONRoutine jsonRoutine = new Gson().fromJson(IOUtils.toString(App.getContext()
                            .getResources()
                            .openRawResource(R.raw.beginner_routine)
            ), JSONRoutine.class);

            mRoutine = new Routine(jsonRoutine);
        } catch(IOException e) {
            Logger.e("Exception when loading Beginner Routine from JSON file: " + e.getMessage());
        }

        mExercise = mRoutine.getLinkedExercises().get(0);

        mRoutineSubject.onNext(mRoutine);
        mExerciseSubject.onNext(mExercise);
    }

    public static RoutineStream getInstance() {
        if(sInstance == null) {
            sInstance = new RoutineStream();
        }

        return sInstance;
    }

    public Routine getRoutine() {
        return mRoutine;
    }

    /**
     * @return Observable that allows to subscribe when the whole routine has changed.
     */
    public Observable<Routine> getRoutineObservable() {
        Observable<Routine> routineObservable = Observable.create(new Observable.OnSubscribe<Routine>() {
            @Override
            public void call(Subscriber<? super Routine> subscriber) {
                subscriber.onNext(mRoutine);
            }
        }).observeOn(AndroidSchedulers.mainThread()).publish().refCount();

        return Observable.merge(mRoutineSubject, routineObservable);
    }

    public void setExercise(Exercise exercise) {
        mExercise = exercise;
        mExerciseSubject.onNext(exercise);
    }

    public Exercise getExercise() {
        return mExercise;
    }

    public void setLevel(Exercise exercise, int level) {
        mRoutine.setLevel(exercise, level);

        setExercise(exercise);

        // We save our current exercise for given section
        // TODO: This should use section id (not title).
        Glacier.put(exercise.getSection().getTitle(), exercise.getSection().getCurrentExercise().getId());

        mLevelChangedSubject.onNext(mRoutine);
    }

    public Observable<Exercise> getExerciseChangedObservable() {
        return mExerciseSubject;
    }

    /**
     * @return Observable that allows to subscribe when only exercise has changed.
     */
    public Observable<Exercise> getExerciseObservable() {
        Observable<Exercise> exerciseObservable = Observable.create(new Observable.OnSubscribe<Exercise>() {
            @Override
            public void call(Subscriber<? super Exercise> subscriber) {
                subscriber.onNext(mExercise);
            }
        }).observeOn(AndroidSchedulers.mainThread()).publish().refCount();

        return Observable.merge(mExerciseSubject, exerciseObservable);
    }

    public Observable<Routine> getLevelChangedObservable() {
        return mLevelChangedSubject;
    }
}
