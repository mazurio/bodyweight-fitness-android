package com.bodyweight.fitness.presenter

import com.bodyweight.fitness.view.AbstractView

import rx.Subscription

import java.io.Serializable
import java.util.*

abstract class AbstractPresenter : Serializable {
    @Transient
    private var mSubscriptions: ArrayList<Subscription> = ArrayList()

    @Transient
    var mView: AbstractView? = null

    open fun bindView(view: AbstractView) {
        mView = view
    }

    open fun removeView() {
        if (mSubscriptions == null) {
            mSubscriptions = ArrayList()
        }

        for (s in mSubscriptions) {
            s.unsubscribe()
        }

        mSubscriptions.clear()
    }

    fun subscribe(subscription: Subscription) {
        if (mSubscriptions == null) {
            mSubscriptions = ArrayList()
        }

        mSubscriptions.add(subscription)
    }
}