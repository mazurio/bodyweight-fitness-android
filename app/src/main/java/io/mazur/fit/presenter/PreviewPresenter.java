package io.mazur.fit.presenter;

import java.io.Serializable;

import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.PreviewView;

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
