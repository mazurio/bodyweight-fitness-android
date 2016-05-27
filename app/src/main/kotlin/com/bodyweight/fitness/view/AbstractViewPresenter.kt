package com.bodyweight.fitness.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.bodyweight.fitness.model.CompletionRate
import com.bodyweight.fitness.model.RepositoryCategory
import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositoryRoutine

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

    fun calculateLayoutWeight(completionRate: Int): Float {
        if (completionRate <= 10) {
            return 7f
        }

        val weight = completionRate * 0.7f;

        if (weight > 70f) {
            return 70f
        }

        return weight
    }
}

abstract class AbstractView : RelativeLayout {
    abstract var presenter: AbstractPresenter

    val superStateKey = this.javaClass.canonicalName + "superState"
    val presenterKey = this.javaClass.canonicalName + "presenter"

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