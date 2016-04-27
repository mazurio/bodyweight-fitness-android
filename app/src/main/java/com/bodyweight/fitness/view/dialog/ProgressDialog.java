package com.bodyweight.fitness.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.model.SectionMode;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.view.widget.CircularProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.bodyweight.fitness.R;

public class ProgressDialog extends DialogFragment {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.level_previous_button)
    ImageButton mLevelPreviousButton;

    @InjectView(R.id.level_next_button)
    ImageButton mLevelNextButton;

    @InjectView(R.id.level_progress_bar)
    CircularProgressBar mLevelProgressBar;

    @InjectView(R.id.level_text_view)
    TextView mLevelTextView;

    @InjectView(R.id.chooseButton)
    Button mLevelConfirmButton;

    private Exercise mExercise;

    private int mAvailableLevels;
    private int mChosenLevel = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String exerciseId = getArguments().getString(Constants.INSTANCE.getExerciseId());

        Routine routine = RoutineStream.getInstance().getRoutine();

        for (Exercise exercise : routine.getExercises()) {
            if (exercise.getExerciseId().equals(exerciseId)) {
                mExercise = exercise;
                mAvailableLevels = mExercise.getSection().getAvailableLevels();
                mChosenLevel = mExercise.getSection().getCurrentLevel();

                break;
            }
        }

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.view_dialog_level);
        dialog.setCanceledOnTouchOutside(true);

        ButterKnife.inject(this, dialog);

        mLevelPreviousButton.setOnClickListener(v -> {
            mChosenLevel = mChosenLevel - 1;
            updateDialog();
        });

        mLevelNextButton.setOnClickListener(v -> {
            mChosenLevel = mChosenLevel + 1;
            updateDialog();
        });

        mLevelConfirmButton.setOnClickListener(v -> {
            Exercise chosenExercise = mExercise.getSection().getExercises().get(mChosenLevel);

            RoutineStream.getInstance().setLevel(chosenExercise, mChosenLevel);

            dialog.dismiss();
        });

        mLevelProgressBar.setWheelSize(12);
        mLevelProgressBar.setProgressColor(Color.parseColor("#009688")); // 00453E
        mLevelProgressBar.setProgressBackgroundColor(Color.parseColor("#00453E"));

        updateDialog();

        return dialog;
    }

    private void updateDialog() {
        Exercise chosenExercise = mExercise.getSection().getExercises().get(mChosenLevel);

        mToolbar.setTitle(chosenExercise.getTitle());
        mToolbar.setSubtitle(chosenExercise.getDescription());

        if(mExercise.getSection().getSectionMode() == SectionMode.LEVELS) {
            mLevelTextView.setText(chosenExercise.getLevel());
        } else {
            mLevelTextView.setText("Pick One");
        }

        if(mChosenLevel == 0) {
            mLevelPreviousButton.setVisibility(View.INVISIBLE);
        } else {
            mLevelPreviousButton.setVisibility(View.VISIBLE);
        }

        if(mChosenLevel >= (mAvailableLevels - 1)) {
            mLevelNextButton.setVisibility(View.INVISIBLE);
        } else {
            mLevelNextButton.setVisibility(View.VISIBLE);
        }

        updateProgressBar();
    }

    private void updateProgressBar() {
        mLevelProgressBar.setProgress((1f / mAvailableLevels) * (mChosenLevel + 1));
    }
}
