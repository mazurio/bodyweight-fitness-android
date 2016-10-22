package com.bodyweight.fitness.view.workout

import android.content.Context
import android.net.Uri
import android.util.AttributeSet

import com.bodyweight.fitness.setInvisible
import com.bodyweight.fitness.setVisible

import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.view.AbstractPresenter
import com.bodyweight.fitness.view.AbstractView

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_workout.view.*

class PreviewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RoutineStream.exerciseObservable().bindToLifecycle(view).subscribe {
            if (it.videoId != "") {
                view.video_view.setVisible()

                val identifier = view.context.resources.getIdentifier(it.videoId, "raw", view.context.packageName)
                val videoUri = Uri.parse("android.resource://" + view.context.packageName + "/" + identifier);

                view.video_view.setVideoURI(videoUri);
                view.video_view.setOnPreparedListener {
                    it.isLooping = true

                    view.video_view.start()
                }
            } else {
                view.video_view.setInvisible()
            }
        }
    }
}

open class PreviewView : AbstractView {
    override var presenter: AbstractPresenter = PreviewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}