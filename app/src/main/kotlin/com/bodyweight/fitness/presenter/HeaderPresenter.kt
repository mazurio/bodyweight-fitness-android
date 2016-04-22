package com.bodyweight.fitness.presenter

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.view.AbstractView
import com.bodyweight.fitness.view.HeaderView

import com.trello.rxlifecycle.kotlin.bindToLifecycle

class HeaderPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.getInstance()
                .routineObservable
                .bindToLifecycle(view)
                .subscribe { setText(it) }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        val routine = RoutineStream.getInstance().routine

        setText(routine)
    }

    fun setText(routine: Routine) {
        val headerView: HeaderView = (mView as HeaderView)

        headerView.setText(routine.title, routine.subtitle)
    }
}