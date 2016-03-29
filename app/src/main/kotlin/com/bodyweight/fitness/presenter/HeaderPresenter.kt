package com.bodyweight.fitness.presenter

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.view.AbstractView
import com.bodyweight.fitness.view.HeaderView

class HeaderPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        subscribe(RoutineStream.getInstance().routineObservable.subscribe { setText(it) })
    }

    open fun setText(routine: Routine) {
        val headerView: HeaderView = (mView as HeaderView)

        headerView.setText(routine.title, routine.subtitle)
    }
}