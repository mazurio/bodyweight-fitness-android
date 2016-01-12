package com.bodyweight.fitness.presenter;

import java.io.Serializable;

import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.view.PreviewView;

public class PreviewPresenter extends IPresenter<PreviewView> implements Serializable {
    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(RoutineStream.getInstance()
                .getExerciseObservable()
                .subscribe(exercise -> {
                    mView.getPreviewGifImageView().setImageResource(
                            mView.getContext()
                                    .getResources()
                                    .getIdentifier(exercise.getId(), "drawable", mView.getContext().getPackageName())
                    );
                }));
    }
}
