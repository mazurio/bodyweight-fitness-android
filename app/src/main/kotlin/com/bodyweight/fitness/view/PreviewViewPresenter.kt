package com.bodyweight.fitness.view

import android.content.Context
import android.util.AttributeSet
import com.bodyweight.fitness.extension.debug

import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.utils.Logger
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.liulishuo.filedownloader.util.FileDownloadUtils

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_preview.view.*
import java.io.File

class PreviewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        getExerciseObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    val routine = RoutineStream.getInstance().routine
                    val imageViewTarget = GlideDrawableImageViewTarget(view.image_view)

                    if (routine.routineId == "routine0") run {
                        val identifier = view.context.resources.getIdentifier(it.id, "drawable", view.context.packageName)

                        try {
                            Glide.with(view.context)
                                    .load(identifier)
                                    .crossFade()
//                                    .into(imageViewTarget)
                        } catch (e: Exception) {
                            Logger.e("Glide exception " + e.message)
                        }
                    } else {
                        val filePath = String.format("%s%s%s%s%s.gif",
                                FileDownloadUtils.getDefaultSaveRootPath(),
                                File.separator,
                                routine.routineId,
                                File.separator,
                                it.gifId)

                        Glide.with(view.context)
                                .load(File(filePath))
                                .crossFade()
//                                .into(imageViewTarget)
                    }
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