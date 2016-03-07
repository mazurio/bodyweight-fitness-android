package com.bodyweight.fitness.stream;

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.json.JSONRoutine;
import com.bodyweight.fitness.utils.Logger;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Routine;

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
    private final PublishSubject<Routine> mRoutineChangedSubject = PublishSubject.create();
    private final PublishSubject<Exercise> mExerciseSubject = PublishSubject.create();
    private final PublishSubject<Routine> mLevelChangedSubject = PublishSubject.create();

    private RoutineStream() {
        // should load default routine instead.
        changeRoutine(R.raw.beginner_routine);
    }

    public static RoutineStream getInstance() {
        if(sInstance == null) {
            sInstance = new RoutineStream();
        }

        return sInstance;
    }

    public void setRoutine(int routineId) {
        if (routineId == 0) {
            changeRoutine(R.raw.beginner_routine);
        } else {
            changeRoutine(R.raw.molding_mobility);
        }

        mRoutineChangedSubject.onNext(mRoutine);
    }

    private void changeRoutine(int resource) {
        try {
            JSONRoutine jsonRoutine = new Gson().fromJson(IOUtils.toString(App.getContext()
                    .getResources()
                    .openRawResource(resource)
            ), JSONRoutine.class);

            mRoutine = new Routine(jsonRoutine);
        } catch(IOException e) {
            Logger.e("Exception when loading Beginner Routine from JSON file: " + e.getMessage());
        }

        mExercise = mRoutine.getLinkedExercises().get(0);

        mRoutineSubject.onNext(mRoutine);
        mExerciseSubject.onNext(mExercise);
    }

    public Routine getRoutine() {
        return mRoutine;
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
        Glacier.put(
                exercise.getSection().getSectionId(),
                exercise.getSection().getCurrentExercise().getExerciseId()
        );

        mLevelChangedSubject.onNext(mRoutine);
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

    public Observable<Routine> getRoutineChangedObservable() {
        return mRoutineChangedSubject;
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
