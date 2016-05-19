package com.bodyweight.fitness

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.view.HeaderPresenter
import com.bodyweight.fitness.view.HeaderView

import org.jetbrains.spek.api.Spek

import org.mockito.Mockito.*
import rx.subjects.PublishSubject
import kotlin.test.assertEquals

class TestSpec: Spek({
    given("HeaderPresenter") {
//        beforeEach {
//            routine = mock(Routine::class.java)
//            headerPresenter = mock(HeaderPresenter::class.java)
//            headerView = mock(HeaderView::class.java)
//            observable = PublishSubject.create()
//
//            `when`(routine.title).thenReturn("Title")
//            `when`(routine.subtitle).thenReturn("Subtitle")
//
//            `when`(headerPresenter.getCurrentRoutine()).thenReturn(routine)
//            `when`(headerPresenter.getRoutineObservable()).thenReturn(observable)
//            `when`(headerPresenter.getView()).thenReturn(headerView)
//        }
//
//        it("creates presenter") {
//            assertEquals(observable, headerPresenter.getRoutineObservable())
//            assertEquals(routine, headerPresenter.getCurrentRoutine())
//            assertEquals(headerView, headerPresenter.getView())
//        }
//
//        it("binds view by setting title and subtitle") {
//            headerPresenter.bindView(headerView)
//            headerPresenter.setText(routine)
//
//            verify(headerView).setText("Title", "Subtitle")
//        }
//
//        it("restores view by setting title and subtitle") {
//            `when`(headerPresenter.restoreView(headerView)).thenCallRealMethod()
//
//            headerPresenter.restoreView(headerView)
//
//            verify(headerView).setText("Title", "Subtitle")
//        }
    }
}) {
    companion object {
        var routine: Routine = mock(Routine::class.java)

        var headerPresenter: HeaderPresenter = mock(HeaderPresenter::class.java)
        var headerView: HeaderView = mock(HeaderView::class.java)

        var observable: PublishSubject<Routine> = PublishSubject.create()
    }
}