package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.model.*

import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.SetReps
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_timer.view.*
import org.joda.time.DateTime
import java.util.*

class RestTimerViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)
    }
}

open class RestTimerView : AbstractView {
    override var presenter: AbstractPresenter = RestTimerViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
    }
}