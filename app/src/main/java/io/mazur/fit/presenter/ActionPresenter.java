package io.mazur.fit.presenter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import io.mazur.fit.R;
import io.mazur.fit.stream.DrawerStream;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.ui.BuyEquipmentActivity;
import io.mazur.fit.ui.ProgressActivity;
import io.mazur.fit.view.ActionView;
import io.mazur.fit.view.dialog.LogWorkoutDialog;
import io.mazur.fit.view.dialog.ProgressDialog;

public class ActionPresenter extends IPresenter<ActionView> {
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
                .filter(id -> id.equals(R.id.action_menu_home) || id.equals(R.id.action_menu_workout_log))
                .subscribe(id -> {
                    if (id.equals(R.id.action_menu_home)) {
                        mView.showActionButtons();
                    } else if (id.equals(R.id.action_menu_workout_log)) {
                        mView.hideActionButtons();
                    }
                }));
    }

    public void onClickLogWorkoutButton() {
        LogWorkoutDialog logWorkoutDialog = new LogWorkoutDialog(getContext());
        logWorkoutDialog.show();
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
        String routineId = RealmStream.getInstance().getRealmRoutineForToday().getId();

        getContext().startActivity(
                new Intent(getContext(), ProgressActivity.class)
                        .putExtra("routineId", routineId)
        );
    }
}
