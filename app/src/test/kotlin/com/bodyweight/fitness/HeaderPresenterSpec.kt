package com.bodyweight.fitness

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.view.HeaderPresenter
import com.bodyweight.fitness.view.HeaderView
import com.bodyweight.fitness.view.getCurrentRoutine
import com.bodyweight.fitness.view.getRoutineObservable

import org.jetbrains.spek.api.Spek
import org.mockito.Mockito
import rx.Observable

import org.mockito.Matchers.any
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy

class HeaderPresenterSpec: Spek() { init {
    given("Header Presenter") {
        on("bindView") {
            it("should set title and subtitle of the header") {
                val routine: Routine = mock(Routine::class.java)

                val presenter: HeaderPresenter = spy(HeaderPresenter())
                val mockView: HeaderView = mock(HeaderView::class.java)

//                doReturn(Observable.just(routine)).`when`(presenter).getRoutineObservable()
//                doReturn(routine).`when`(presenter).getCurrentRoutine()

                mockView.setText("Mock", "Mock")
                verify(mockView).setText("Mock Title", "Mock Subtitle")
            }
        }
    }
}}