package com.bodyweight.fitness.stream

import com.bodyweight.fitness.App
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.persistence.Glacier
import com.bodyweight.fitness.utils.Preferences

import com.google.gson.Gson

import org.apache.commons.io.IOUtils

import java.io.IOException

import com.bodyweight.fitness.R

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.PublishSubject

class JsonRoutineLoader {
    fun getRoutine(resource: Int): Routine {
        try {
            val raw = IOUtils.toString(App.context!!.resources.openRawResource(resource))
            val jsonRoutine = Gson().fromJson(raw, JSONRoutine::class.java)

            return Routine(jsonRoutine)
        } catch (e: IOException) {}

        return Routine(JSONRoutine())
    }
}

object RoutineStream {
    val routine: Routine by lazy {
        val loader = JsonRoutineLoader()
        val routine: Routine = loader.getRoutine(R.raw.bodyweight_fitness_recommended_routine)

        Preferences.defaultRoutine = routine.routineId

        exercise = routine.linkedExercises.first()

        mRoutineSubject.onNext(routine)
        mExerciseSubject.onNext(exercise)

        mRoutineChangedSubject.onNext(routine)

        routine
    }

    var exercise: Exercise = routine.linkedExercises.first()
        set(exercise) {
            this.exercise = exercise
            mExerciseSubject.onNext(exercise)
        }

    private val mRoutineSubject = PublishSubject.create<Routine>()
    private val mRoutineChangedSubject = PublishSubject.create<Routine>()
    private val mExerciseSubject = PublishSubject.create<Exercise>()
    private val mLevelChangedSubject = PublishSubject.create<Routine>()

    fun setLevel(exercise: Exercise, level: Int) {
        routine.setLevel(exercise, level)

        this.exercise = exercise

        Glacier.put(exercise.section!!.sectionId, exercise.section!!.currentExercise.exerciseId)

        mLevelChangedSubject.onNext(routine)
    }

    val routineObservable: Observable<Routine>
        get() {

            val routineObservable = Observable.just<Routine>(routine)
                    .observeOn(AndroidSchedulers.mainThread())
                    .publish()
                    .refCount()

            return Observable.merge(mRoutineSubject, routineObservable)
        }

    val routineChangedObservable: Observable<Routine>
        get() = mRoutineChangedSubject

    val exerciseObservable: Observable<Exercise>
        get() {
            val exerciseObservable = Observable.just<Exercise>(exercise)
                    .observeOn(AndroidSchedulers.mainThread())
                    .publish()
                    .refCount()

            return Observable.merge(mExerciseSubject, exerciseObservable)
        }
}
