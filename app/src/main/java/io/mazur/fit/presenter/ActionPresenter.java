package io.mazur.fit.presenter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.ui.BuyEquipmentActivity;
import io.mazur.fit.ui.ProgressActivity;
import io.mazur.fit.utils.Logger;
import io.mazur.fit.view.ActionView;
import io.mazur.fit.view.dialog.LogWorkoutDialog;
import io.mazur.fit.view.dialog.ProgressDialog;
import rx.Observable;

public class ActionPresenter {
    private transient ActionView mActionView;

    public void onCreateView(ActionView actionView) {
        mActionView = actionView;

        RoutineStream.getInstance()
                .getExerciseObservable()
                .subscribe(exercise -> {
                    if (exercise.hasProgressions()) {
                        mActionView.setActionButtonImageDrawable(R.drawable.ic_assessment_white_24dp);
                        mActionView.showActionSheetChooseProgression();
                    } else {
                        mActionView.setActionButtonImageDrawable(R.drawable.ic_add_white_24dp);
                        mActionView.hideActionSheetChooseProgression();
                    }
                });
    }

    public void onClickLogWorkoutButton() {
        LogWorkoutDialog logWorkoutDialog = new LogWorkoutDialog(mActionView.getContext());
        logWorkoutDialog.show();
    }

    public void onClickBuyEquipment() {
        mActionView.getContext().startActivity(
                new Intent(mActionView.getContext(), BuyEquipmentActivity.class)
        );
    }

    public void onClickWatchOnYouTube() {
        String id = RoutineStream.getInstance().getExercise().getYouTubeId();

        if(id != null) {
            try {
                mActionView.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id))
                );
            } catch(ActivityNotFoundException e) {
                mActionView.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id))
                );
            }
        }
    }

    public void onClickChooseProgression() {
        ProgressDialog progressDialog = new ProgressDialog(mActionView.getContext(), RoutineStream.getInstance().getExercise());
        progressDialog.show();
    }

    public void onClickLogWorkout() {
        String routineId = RealmStream.getInstance().getRealmRoutineForToday().getId();

        mActionView.getContext().startActivity(
                new Intent(mActionView.getContext(), ProgressActivity.class)
                        .putExtra("routineId", routineId)
        );
    }
}
