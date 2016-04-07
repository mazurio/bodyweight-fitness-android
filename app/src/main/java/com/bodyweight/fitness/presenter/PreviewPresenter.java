package com.bodyweight.fitness.presenter;

import java.io.File;
import java.io.Serializable;

import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.view.PreviewView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import com.liulishuo.filedownloader.util.FileDownloadUtils;

public class PreviewPresenter extends IPresenter<PreviewView> implements Serializable {
    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(RoutineStream.getInstance()
                .getExerciseObservable()
                .subscribe(exercise -> {
                    Routine routine = RoutineStream.getInstance().getRoutine();

                    GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mView.getImageView());

                    if (routine.getRoutineId().equals("routine0")) {
                        int identifier = mView.getContext()
                                .getResources()
                                .getIdentifier(exercise.getId(), "drawable", mView.getContext().getPackageName());

                        Glide.with(getContext())
                                .load(identifier)
                                .crossFade()
                                .into(imageViewTarget);
                    } else {
                        String filePath = String.format("%s%s%s%s%s.gif",
                                FileDownloadUtils.getDefaultSaveRootPath(),
                                File.separator,
                                routine.getRoutineId(),
                                File.separator,
                                exercise.getGifId());

                        Glide.with(getContext())
                                .load(new File(filePath))
                                .crossFade()
                                .into(imageViewTarget);
                    }
                }));
    }
}
