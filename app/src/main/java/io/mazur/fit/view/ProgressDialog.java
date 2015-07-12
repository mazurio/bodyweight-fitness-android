package io.mazur.fit.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.SectionMode;
import io.mazur.fit.stream.RoutineStream;

public class ProgressDialog {
    @InjectView(R.id.level_title_text_view) TextView mLevelTitleTextView;
    @InjectView(R.id.level_previous_button) ImageButton mLevelPreviousButton;
    @InjectView(R.id.level_next_button) ImageButton mLevelNextButton;
    @InjectView(R.id.level_progress_bar) CircularProgressBar mLevelProgressBar;
    @InjectView(R.id.level_text_view) TextView mLevelTextView;
    @InjectView(R.id.level_confirm_button) FloatingActionButton mLevelConfirmButton;

    private Dialog mDialog;

    private Exercise mExercise;

    private int mAvailableLevels;
    private int mChosenLevel = 0;

    public ProgressDialog(Context context, Exercise exercise) {
        mExercise = exercise;
        mAvailableLevels = mExercise.getSection().getAvailableLevels();
        mChosenLevel = mExercise.getSection().getCurrentLevel();

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_level);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        ButterKnife.inject(this, mDialog);

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

            mDialog.dismiss();
        });

        mLevelProgressBar.setWheelSize(12);
        mLevelProgressBar.setProgressColor(Color.parseColor("#009688")); // 00453E
        mLevelProgressBar.setProgressBackgroundColor(Color.parseColor("#00453E"));

        updateDialog();

        mDialog.show();
    }

    private void updateDialog() {
        Exercise chosenExercise = mExercise.getSection().getExercises().get(mChosenLevel);

        if(mExercise.getSection().getSectionMode() == SectionMode.LEVELS) {
            mLevelTitleTextView.setText(chosenExercise.getTitle());
            mLevelTextView.setText(chosenExercise.getLevel());
        } else {
            mLevelTitleTextView.setText(chosenExercise.getTitle());
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
