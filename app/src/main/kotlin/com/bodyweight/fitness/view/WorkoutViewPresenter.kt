package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

class WorkoutViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)
    }
}

open class WorkoutView : AbstractView {
    override var mPresenter: AbstractPresenter = WorkoutViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        super.onCreateView()
    }
}