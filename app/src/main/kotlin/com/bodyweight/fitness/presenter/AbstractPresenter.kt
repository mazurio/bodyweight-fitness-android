package com.bodyweight.fitness.presenter

import com.bodyweight.fitness.view.AbstractView

import rx.Subscription

import java.io.Serializable
import java.util.*

abstract class AbstractPresenter : Serializable {
    @Transient
    private val mSubscriptions: ArrayList<Subscription> = ArrayList()

    @Transient
    var mView: AbstractView? = null

    open fun bindView(view: AbstractView) {
        mView = view
    }

    open fun removeView() {
        for (s in mSubscriptions) {
            s.unsubscribe()
        }

        mSubscriptions.clear()
    }

    fun subscribe(subscription: Subscription) {
        mSubscriptions.add(subscription)
    }
}