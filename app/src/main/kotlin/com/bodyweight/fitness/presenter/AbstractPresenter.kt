package com.bodyweight.fitness.presenter

import com.bodyweight.fitness.view.AbstractView

import rx.Subscription

import java.io.Serializable
import java.util.*

abstract class AbstractPresenter : Serializable {
    @Transient
    var mView: AbstractView? = null

    open fun bindView(view: AbstractView) {
        mView = view
    }

    open fun restoreView(view: AbstractView) {
        mView = view
    }
}