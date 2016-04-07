package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.presenter.AbstractPresenter
import com.bodyweight.fitness.presenter.HeaderPresenter

import kotlinx.android.synthetic.main.activity_main_header.view.*

open class HeaderView : AbstractView {
    override var mAbstractPresenter: AbstractPresenter = HeaderPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setText(title: String, subtitle: String) {
        routine_title.text = title
        routine_subtitle.text = subtitle
    }
}