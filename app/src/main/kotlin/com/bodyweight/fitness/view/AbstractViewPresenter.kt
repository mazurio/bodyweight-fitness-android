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
    abstract var presenter: AbstractPresenter

    val superStateKey = "superState"
    val presenterKey = "presenter"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.onCreateView()

        presenter.bindView(this)
    }

    open fun onCreateView() {}

    open fun updateView() {
        presenter.updateView()
    }

    override fun onSaveInstanceState(): Parcelable? {
        presenter.saveView()

        val state = Bundle()

        state.putParcelable(superStateKey, super.onSaveInstanceState());
        state.putSerializable(presenterKey, presenter);

        return state;
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            presenter = state.getSerializable(presenterKey) as AbstractPresenter

            super.onRestoreInstanceState(state.getParcelable(superStateKey))
        }

        presenter.restoreView(this)
    }
}