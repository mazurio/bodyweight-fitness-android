package com.bodyweight.fitness.presenter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.stream.DrawerStream;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.view.ActionView;
import com.bodyweight.fitness.view.dialog.LogWorkoutDialog;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.ui.BuyEquipmentActivity;
import com.bodyweight.fitness.ui.ProgressActivity;
import com.bodyweight.fitness.view.dialog.ProgressDialog;

public class ActionPresenter extends IPresenter<ActionView> {
    private transient LogWorkoutDialog mLogWorkoutDialog;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mLogWorkoutDialog != null) {
            mLogWorkoutDialog.dismiss();
            mLogWorkoutDialog = null;
        }
    }

    @Override
    public void onSaveInstanceState() {
        super.onSaveInstanceState();

        if (mLogWorkoutDialog != null) {
            mLogWorkoutDialog.dismiss();
            mLogWorkoutDialog = null;
        }
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

       subscribe(RoutineStream.getInstance()
               .getExerciseObservable()
               .subscribe(exercise -> {
                   if (exercise.hasProgressions()) {
                       mView.setActionButtonImageDrawable(R.drawable.ic_assessment_white_24dp);
                       mView.showActionSheetChooseProgression();
                   } else {
                       mView.setActionButtonImageDrawable(R.drawable.ic_add_white_24dp);
                       mView.hideActionSheetChooseProgression();
                   }
               }));

        subscribe(DrawerStream.getInstance()
                .getMenuObservable()
                .filter(id ->
                        id.equals(R.id.action_menu_home) ||
                        id.equals(R.id.action_menu_change_routine) ||
                        id.equals(R.id.action_menu_workout_log)
                )
                .subscribe(id -> {
                    if (id.equals(R.id.action_menu_home)) {
                        mView.showActionButtons();
                    } else {
                        mView.hideActionButtons();
                    }
                }));
    }

    public void onClickLogWorkoutButton() {
        mLogWorkoutDialog = new LogWorkoutDialog(getContext());
        mLogWorkoutDialog.show();
    }

    public void onClickBuyEquipment() {
        getContext().startActivity(
                new Intent(getContext(), BuyEquipmentActivity.class)
        );
    }

    public void onClickWatchOnYouTube() {
        String id = RoutineStream.getInstance().getExercise().getYouTubeId();

        if(id != null) {
            try {
                getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id))
                );
            } catch(ActivityNotFoundException e) {
                getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id))
                );
            }
        }
    }

    public void onClickChooseProgression() {
        ProgressDialog progressDialog = new ProgressDialog(getContext(), RoutineStream.getInstance().getExercise());
        progressDialog.show();
    }

    public void onClickLogWorkout() {
        String routineId = RepositoryStream.getInstance().getRepositoryRoutineForToday().getId();

        getContext().startActivity(
                new Intent(getContext(), ProgressActivity.class)
                        .putExtra(Constants.PRIMARY_KEY_ROUTINE_ID, routineId)
        );
    }
}
