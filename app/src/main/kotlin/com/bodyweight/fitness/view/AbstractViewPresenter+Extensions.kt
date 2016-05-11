package com.bodyweight.fitness.view

import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.stream.RoutineStream

import rx.Observable

fun AbstractPresenter.getCurrentExercise(): Exercise =
        RoutineStream.exercise

fun AbstractPresenter.getExerciseObservable(): Observable<Exercise> =
        RoutineStream.exerciseObservable