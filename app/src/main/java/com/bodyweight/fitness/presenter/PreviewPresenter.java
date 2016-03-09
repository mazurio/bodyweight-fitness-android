package com.bodyweight.fitness.presenter;

import java.io.File;
import java.io.Serializable;

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

                    String savePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "ted.jpg";

                    Logger.d(savePath);

                    Picasso.with(getContext())
                            .load(savePath)
                            .into(mView.getPreviewGifImageView());

//                    mView.getPreviewGifImageView().setImageResource(
//                            mView.getContext()
//                                    .getResources()
//                                    .getIdentifier(exercise.getId(), "drawable", mView.getContext().getPackageName())
//                    );
                }));
    }
}
