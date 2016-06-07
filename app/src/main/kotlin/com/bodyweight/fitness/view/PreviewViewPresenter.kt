package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet

import com.bodyweight.fitness.model.Exercise
import com.bodyweight.fitness.stream.RoutineStream

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_preview.view.*
import rx.Subscriber

class PreviewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe(object: Subscriber<Exercise>(){
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        error("Glide Exception = " + e.message)
                    }

                    override fun onNext(it: Exercise) {
                        val imageViewTarget = GlideDrawableImageViewTarget(view.image_view)
                        val identifier = view.context.resources.getIdentifier(it.id, "drawable", view.context.packageName)

                        Glide.with(view.context)
                                .load(identifier)
                                .crossFade()
                                .into(imageViewTarget)
                    }
                })
    }
}

open class PreviewView : AbstractView {
    override var presenter: AbstractPresenter = PreviewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {}
}