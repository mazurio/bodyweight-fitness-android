package com.bodyweight.fitness.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.RelativeLayout

import java.io.Serializable

abstract class AbstractPresenter : Serializable {
    @Transient
    var mView: AbstractView? = null

    open fun bindView(view: AbstractView) {
        mView = view
    }

    open fun saveView() {}

    open fun restoreView(view: AbstractView) {
        mView = view
    }

    open fun updateView() {}

    open fun getView(): AbstractView {
        return mView!!
    }

    open fun context(): Context {
        return mView!!.context
    }
}

abstract class AbstractView : RelativeLayout {
    abstract var mPresenter: AbstractPresenter

    val superStateKey = "superState"
    val presenterKey = "presenter"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.onCreateView()

        mPresenter.bindView(this)
    }

    open fun onCreateView() {}

    open fun updateView() {
        mPresenter.updateView()
    }

    override fun onSaveInstanceState(): Parcelable? {
        mPresenter.saveView()

        val state = Bundle()

        state.putParcelable(superStateKey, super.onSaveInstanceState());
        state.putSerializable(presenterKey, mPresenter);

        return state;
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            mPresenter = state.getSerializable(presenterKey) as AbstractPresenter

            super.onRestoreInstanceState(state.getParcelable(superStateKey))
        }

        mPresenter.restoreView(this)
    }
}