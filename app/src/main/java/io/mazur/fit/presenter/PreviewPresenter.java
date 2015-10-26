package io.mazur.fit.presenter;

import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.dialog.LogWorkoutDialog;
import io.mazur.fit.view.PreviewView;
import io.mazur.fit.view.dialog.ProgressDialog;

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

    public void onLogWorkoutButtonClick() {
        LogWorkoutDialog logWorkoutDialog = new LogWorkoutDialog(mPreviewView.getContext());
        logWorkoutDialog.show();
    }

    public void onActionButtonClick() {
        ProgressDialog progressDialog = new ProgressDialog(mPreviewView.getContext(), RoutineStream.getInstance().getExercise());
        progressDialog.show();
    }
}
