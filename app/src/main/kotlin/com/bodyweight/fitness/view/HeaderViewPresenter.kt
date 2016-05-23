package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.model.Routine
import com.bodyweight.fitness.stream.RoutineStream

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_main_header.view.*

open class HeaderPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.routineObservable()
                .bindToLifecycle(view)
                .subscribe {
                    setText(it)
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        setText(RoutineStream.routine)
    }

    override fun getView(): HeaderView {
        return (mView as HeaderView)
    }

    fun setText(routine: Routine) {
        getView().setText(routine.title, routine.subtitle)
    }
}

open class HeaderView : AbstractView {
    override var presenter: AbstractPresenter = HeaderPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    open fun setText(title: String, subtitle: String) {
        routine_title.text = title
        routine_subtitle.text = subtitle
    }
}