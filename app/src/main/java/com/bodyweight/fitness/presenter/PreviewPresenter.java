package com.bodyweight.fitness.presenter;

import java.io.File;
import java.io.Serializable;

import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;
import com.bodyweight.fitness.view.PreviewView;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.squareup.picasso.Picasso;

public class PreviewPresenter extends IPresenter<PreviewView> implements Serializable {
    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(RoutineStream.getInstance()
                .getExerciseObservable()
                .subscribe(exercise -> {
                    Routine routine = RoutineStream.getInstance().getRoutine();

                    if (routine.getRoutineId().equals("routine0")) {
//                        mView.getPreviewGifImageView().setImageResource(
//                            mView.getContext()
//                                    .getResources()
//                                    .getIdentifier(exercise.getId(), "drawable", mView.getContext().getPackageName())
//                        );
                    } else {
                        String filePath = String.format("%s%s%s%s%s.gif",
                                FileDownloadUtils.getDefaultSaveRootPath(),
                                File.separator,
                                routine.getRoutineId(),
                                File.separator,
                                exercise.getGifId());

                        Logger.d(filePath);

//                        Picasso.with(getContext())
//                                .load(new File(filePath))
//                                .into(mView.getPreviewGifImageView());

                        // TODO: Picasso errors image.
                    }
                }));
    }
}
