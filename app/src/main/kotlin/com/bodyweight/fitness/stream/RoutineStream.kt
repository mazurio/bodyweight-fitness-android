package com.bodyweight.fitness.stream

import com.bodyweight.fitness.App
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.persistence.Glacier
import com.bodyweight.fitness.utils.Preferences

import com.google.gson.Gson

import org.apache.commons.io.IOUtils

import java.io.IOException

import com.bodyweight.fitness.R
import com.bodyweight.fitness.adapter.SpinnerRoutine
import com.bodyweight.fitness.extension.debug

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.PublishSubject

class JsonRoutineLoader {
    fun getRoutine(resource: Int): Routine {
        try {
            val raw = IOUtils.toString(App.context!!.resources.openRawResource(resource))
            val jsonRoutine = Gson().fromJson(raw, JSONRoutine::class.java)

            return Routine(jsonRoutine)
        } catch (e: IOException) {
            error(e.message.toString())
        }
    }
}

object RoutineStream {
    private val routineSubject = PublishSubject.create<Routine>()
    private val exerciseSubject = PublishSubject.create<Exercise>()

    var routine: Routine = JsonRoutineLoader().getRoutine(R.raw.bodyweight_fitness_recommended_routine)
        set(value) {
            if (value.routineId.equals(routine.routineId)) {
                return
            }

            Preferences.defaultRoutine = value.routineId

            exercise = value.linkedExercises.first()

            routineSubject.onNext(value)

            field = value

            debug("set value of: " + routine.title)
        }

    var exercise: Exercise = routine.linkedExercises.first()
        set(value) {
            exerciseSubject.onNext(value)

            field = value
        }

    fun setRoutine(spinnerRoutine: SpinnerRoutine) {
        when(spinnerRoutine.id) {
            0 -> {
                routine = JsonRoutineLoader().getRoutine(R.raw.bodyweight_fitness_recommended_routine)
            }
            1 -> {
                routine = JsonRoutineLoader().getRoutine(R.raw.molding_mobility_flexibility_routine)
            }
        }
    }

    fun routineObservable(): Observable<Routine> {
        return Observable.merge(Observable.just(routine).publish().refCount(), routineSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount()
    }

    fun exerciseObservable(): Observable<Exercise> {
        return Observable.merge(Observable.just(exercise).publish().refCount(), exerciseSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount()
    }

    fun setLevel(chosenExercise: Exercise, level: Int) {
        routine.setLevel(chosenExercise, level)

        exercise = chosenExercise

        Glacier.put(chosenExercise.section!!.sectionId, chosenExercise.section!!.currentExercise.exerciseId)
    }
}
