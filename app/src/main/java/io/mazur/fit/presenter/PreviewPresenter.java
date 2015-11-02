package io.mazur.fit.presenter;

import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.PreviewView;

public class PreviewPresenter {
    private transient PreviewView mPreviewView;

    public void onCreateView(PreviewView previewView) {
        mPreviewView = previewView;

        RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
            mPreviewView.getPreviewGifImageView().setImageResource(
                    mPreviewView.getContext()
                            .getResources()
                            .getIdentifier(exercise.getId(), "drawable", mPreviewView.getContext().getPackageName())
            );
        });
    }
}
