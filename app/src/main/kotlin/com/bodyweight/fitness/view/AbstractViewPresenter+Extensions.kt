package com.bodyweight.fitness.view

import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.stream.RoutineStream

import rx.Observable

fun AbstractPresenter.getCurrentRoutine(): Routine =
        RoutineStream.getInstance().routine

fun AbstractPresenter.getCurrentExercise(): Exercise =
        RoutineStream.getInstance().exercise

fun AbstractPresenter.getRoutineObservable(): Observable<Routine> =
        RoutineStream.getInstance().routineObservable

fun AbstractPresenter.getExerciseObservable(): Observable<Exercise> =
        RoutineStream.getInstance().exerciseObservable