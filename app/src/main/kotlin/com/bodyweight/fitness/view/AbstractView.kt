package com.bodyweight.fitness.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.bodyweight.fitness.presenter.AbstractPresenter

abstract class AbstractView : RelativeLayout {
    abstract var mAbstractPresenter: AbstractPresenter

    val superStateKey = "superState"
    val presenterKey = "presenter"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        mAbstractPresenter.bindView(this)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = Bundle()

        state.putParcelable(superStateKey, super.onSaveInstanceState());
        state.putSerializable(presenterKey, mAbstractPresenter);

        return state;
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            mAbstractPresenter = state.getSerializable(presenterKey) as AbstractPresenter

            super.onRestoreInstanceState(state.getParcelable(superStateKey))
        }

        mAbstractPresenter.restoreView(this)
    }
}