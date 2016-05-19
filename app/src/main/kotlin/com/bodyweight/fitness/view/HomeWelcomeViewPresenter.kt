package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.stream.UiEvent

import kotlinx.android.synthetic.main.view_home_welcome.view.*

class HomeWelcomeViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)
    }

    fun startWorkout() {
        UiEvent.showHomePage(1)
    }
}

open class HomeWelcomeView : AbstractView {
    override var mPresenter: AbstractPresenter = HomeWelcomeViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        this.start_workout.setOnClickListener {
            val presenter = mPresenter as HomeWelcomeViewPresenter

            presenter.startWorkout()
        }
    }
}