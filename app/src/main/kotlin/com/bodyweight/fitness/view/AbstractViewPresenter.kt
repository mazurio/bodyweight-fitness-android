package com.bodyweight.fitness.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.RelativeLayout
import rx.Subscription
import java.io.Serializable
import java.util.*

abstract class Presenter : Serializable {
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

abstract class AbstractView : RelativeLayout {
    abstract var mPresenter: Presenter

    val superStateKey = "superState"
    val presenterKey = "presenter"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        mPresenter.bindView(this)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = Bundle()

        state.putParcelable(superStateKey, super.onSaveInstanceState());
        state.putSerializable(presenterKey, mPresenter);

        return state;
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        mPresenter.removeView()

        if (state is Bundle) {
            mPresenter = state.getSerializable(presenterKey) as Presenter

            super.onRestoreInstanceState(state.getParcelable(superStateKey))
        }

        mPresenter.bindView(this)
    }
}