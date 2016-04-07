package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.presenter.AbstractPresenter
import com.bodyweight.fitness.presenter.ToolbarPresenter

import kotlinx.android.synthetic.main.view_toolbar.view.*

class ToolbarView : AbstractView {
    override var mAbstractPresenter: AbstractPresenter = ToolbarPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun inflateHomeMenu() {
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.home)
    }

    fun inflateWorkoutLogMenu() {
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.calendar)
    }

    fun inflateChangeRoutineMenu() {
        toolbar.menu.clear()
    }

    fun setSingleTitle(text: String) {
        toolbar_layout.visibility = GONE
        toolbar.title = text
    }

    fun setTitle(text: String) {
        toolbar_layout.visibility = VISIBLE
        toolbar_exercise_title.text = text
    }

    fun setSubtitle(text: String) {
        toolbar_layout.visibility = VISIBLE
        toolbar_section_title.text = text
    }

    fun setDescription(text: String) {
        toolbar_layout.visibility = VISIBLE
        toolbar_exercise_description.text = text
    }
}