package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import com.bodyweight.fitness.extension.debug

import com.bodyweight.fitness.stream.RoutineStream

import com.trello.rxlifecycle.kotlin.bindToLifecycle

//class TimerPresenter : AbstractPresenter() {
//    override fun bindView(view: AbstractView) {
//        super.bindView(view)
//
//        RoutineStream.getInstance()
//                .exerciseObservable
//                .bindToLifecycle(view)
//                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
//                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
//                .subscribe {
//
//                }
//    }
//}
//
//open class TimerView : AbstractView {
//    override var mPresenter: AbstractPresenter = TimerPresenter()
//
//    constructor(context: Context) : super(context)
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//}