package io.mazur.fit.stream;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import io.mazur.fit.App;
import io.mazur.fit.R;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.RoutineType;
import io.mazur.fit.utils.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class RoutineStream {
    private static RoutineStream sInstance;

    private Routine mRoutine;
    private Routine.PartRoutine mExercise;

    private final PublishSubject<Routine> mRoutineSubject = PublishSubject.create();
    private final PublishSubject<Routine.PartRoutine> mExerciseSubject = PublishSubject.create();

    private RoutineStream() {
        try {
            mRoutine = new Gson().fromJson(IOUtils.toString(App.getContext()
                    .getResources()
                    .openRawResource(R.raw.beginner_routine)
            ), Routine.class);

            /**
             * TODO: Link Routine, I don't like how this is done at the moment.
             * TODO: There is definitely a better way.
             *
             * Allows to link objects inside the Array List together so we can have previous
             * and next buttons inside the application.
             */
            Routine.PartRoutine prev = null;
            int index = 0, currentSectionPosition = 0;

            for(Routine.PartRoutine partRoutine : mRoutine.getPartRoutines()) {
                if(partRoutine.getType() == RoutineType.SECTION) {
                    currentSectionPosition = index;
                }

                if(partRoutine.getType() == RoutineType.EXERCISE) {


                    if(prev != null) {
                        partRoutine.setSectionPosition(currentSectionPosition);
                        partRoutine.setPrevious(prev);

                        prev.setNext(partRoutine);
                    }

                    prev = partRoutine;
                }

                index++;
            }

            mExercise = mRoutine.getFirstExercise();

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

    public void setExercise(Routine.PartRoutine exercise) {
        mExercise = exercise;
        mExerciseSubject.onNext(exercise);
    }

    public Observable<Routine.PartRoutine> getExerciseChangedObservable() {
        return mExerciseSubject;
    }

    /**
     * @return Observable that allows to subscribe when only exercise has changed.
     */
    public Observable<Routine.PartRoutine> getExerciseObservable() {
        Observable<Routine.PartRoutine> exerciseObservable = Observable.create(new Observable.OnSubscribe<Routine.PartRoutine>() {
            @Override
            public void call(Subscriber<? super Routine.PartRoutine> subscriber) {
                subscriber.onNext(mExercise);
            }
        }).observeOn(AndroidSchedulers.mainThread()).publish().refCount();

        return Observable.merge(mExerciseSubject, exerciseObservable);
    }
}
