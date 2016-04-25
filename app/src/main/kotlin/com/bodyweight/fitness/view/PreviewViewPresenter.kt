package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import com.bodyweight.fitness.extension.debug

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_preview.view.*

class PreviewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        getExerciseObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    val imageViewTarget = GlideDrawableImageViewTarget(view.image_view)
                    val identifier = view.context.resources.getIdentifier(it.id, "drawable", view.context.packageName)

                    try {
                        Glide.with(view.context)
                                .load(identifier)
                                .crossFade()
                                .into(imageViewTarget)
                    } catch (e: Exception) { }
                }
    }
}

open class PreviewView : AbstractView {
    override var mPresenter: AbstractPresenter = PreviewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {}
}