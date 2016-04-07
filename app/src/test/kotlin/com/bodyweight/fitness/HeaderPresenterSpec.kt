package com.bodyweight.fitness

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.presenter.HeaderPresenter
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.view.HeaderView
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify

import org.jetbrains.spek.api.Spek

import kotlin.test.assertEquals

class HeaderPresenterSpec: Spek() { init {
    given("Header Presenter") {
        on("bindView") {
            val routine: Routine = mock()

            routine.title = "Mock Title"
            routine.subtitle = "Mock Subtitle"

            it("should set title and subtitle of the header") {
                val presenter = HeaderPresenter()
                val mockView: HeaderView = mock()

                presenter.bindView(mockView)
                presenter.setText(routine)

                verify(mockView).setText("Mock Title", "Mock Subtitle")
            }
        }
    }
}}