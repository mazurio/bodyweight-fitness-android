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
import rx.observables.ConnectableObservable
import rx.subjects.PublishSubject
import kotlin.properties.Delegates

class JsonRoutineLoader {
    fun getRoutine(resource: Int): Routine {
        try {
            val raw = IOUtils.toString(App.context!!.resources.openRawResource(resource))
            val jsonRoutine = Gson().fromJson(raw, JSONRoutine::class.java)

            return Routine(jsonRoutine)
        } catch (e: IOException) {
            error(e.message.toString())
        }

        return Routine(JSONRoutine())
    }
}

object RoutineStream {
    private val routineSubject = PublishSubject.create<Routine>()
    private val exerciseSubject = PublishSubject.create<Exercise>()

    var routine: Routine = JsonRoutineLoader().getRoutine(R.raw.bodyweight_fitness_recommended_routine)
        set(value) {
            Preferences.defaultRoutine = routine.routineId

            exercise = routine.linkedExercises.first()

            routineSubject.onNext(routine)

            field = value
        }

    var exercise: Exercise = routine.linkedExercises.first()
        set(value) {
            exerciseSubject.onNext(value)

            field = value
        }

    val routineObservable: Observable<Routine> =
            Observable.merge(Observable.just(routine), routineSubject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .publish()
                    .refCount()

    val exerciseObservable: Observable<Exercise> =
            Observable.merge(Observable.just(exercise), exerciseSubject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .publish()
                    .refCount()

    fun setLevel(chosenExercise: Exercise, level: Int) {
        routine.setLevel(chosenExercise, level)

        exercise = chosenExercise

        Glacier.put(chosenExercise.section!!.sectionId, chosenExercise.section!!.currentExercise.exerciseId)
    }
}
