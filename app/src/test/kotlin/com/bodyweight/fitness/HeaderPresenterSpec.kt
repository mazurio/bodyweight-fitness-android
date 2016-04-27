package com.bodyweight.fitness

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.view.HeaderPresenter

import org.jetbrains.spek.api.Spek
import rx.Observable

import org.mockito.Mockito.*
import kotlin.test.assertEquals

class TestSpec: Spek({
    given("HeaderPresenter") {
        beforeEach {
            routine = mock(Routine::class.java)
            headerPresenter = mock(HeaderPresenter::class.java)
            observable = Observable.just(routine)
        }

        on("initialize") {
            `when`(headerPresenter.getCurrentRoutine()).thenReturn(routine)
            `when`(headerPresenter.getRoutineObservable()).thenReturn(observable)

            assertEquals(observable, headerPresenter.getRoutineObservable())
            assertEquals(routine, headerPresenter.getCurrentRoutine())
        }
    }
}) {
    companion object {
        var routine: Routine = mock(Routine::class.java)
        var headerPresenter: HeaderPresenter = mock(HeaderPresenter::class.java)

        var observable: Observable<Routine> = Observable.just(routine)
    }
}