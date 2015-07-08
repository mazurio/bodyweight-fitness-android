package io.mazur.fit.stream;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import io.mazur.fit.App;
import io.mazur.fit.R;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.JSONRoutine;
import io.mazur.fit.utils.Logger;

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

    private RoutineStream() {
        try {
            /**
             * This should be read from Glacier if exists.
             */
            JSONRoutine jsonRoutine = new Gson().fromJson(IOUtils.toString(App.getContext()
                    .getResources()
                    .openRawResource(R.raw.beginner_routine)
            ), JSONRoutine.class);

            mRoutine = new Routine(jsonRoutine);
            mExercise = mRoutine.getLinkedExercises().get(0);

            mRoutineSubject.onNext(mRoutine);
            mExerciseSubject.onNext(mExercise);
        } catch (IOException e) {
            Logger.e("Exception when loading Beginner Routine from JSON file: " + e);
        }
    }

    public static RoutineStream getInstance() {
        if(sInstance == null) {
            sInstance = new RoutineStream();
        }

        return sInstance;
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
}
