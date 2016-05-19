package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.stream.UiEvent

import kotlinx.android.synthetic.main.view_home_summary.view.*

class HomeSummaryViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)
    }

    fun backToWorkout() {
        UiEvent.showHomePage(1)
    }
}

open class HomeSummaryView : AbstractView {
    override var mPresenter: AbstractPresenter = HomeSummaryViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        this.back_to_workout_button.setOnClickListener {
            val presenter = mPresenter as HomeSummaryViewPresenter

            presenter.backToWorkout()
        }
    }
}