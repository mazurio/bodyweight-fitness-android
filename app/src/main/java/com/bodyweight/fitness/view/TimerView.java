package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.presenter.TimerPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import butterknife.OnClick;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.utils.ViewUtils;

public class TimerView extends LinearLayout {
    TimerPresenter mPresenter;

    @InjectView(R.id.prev_exercise_button)
    ImageButton mPrevExerciseButton;

    @InjectView(R.id.next_exercise_button)
    ImageButton mNextExerciseButton;

    @InjectView(R.id.timer_minutes)
    TextView mTimerMinutesTextView;

    @InjectView(R.id.timer_seconds)
    TextView mTimerSecondsTextView;

    @InjectView(R.id.increase_timer_button)
    FloatingActionButton mIncreaseTimerButton;

    @InjectView(R.id.start_stop_timer_button)
    FloatingActionButton mStartStopTimerButton;

    @InjectView(R.id.restart_timer_button)
    FloatingActionButton mRestartTimerButton;

    public TimerView(Context context) {
        super(context);

        onCreate();
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        ViewUtils.resetFloatingActionButtonMargin(mIncreaseTimerButton);
        ViewUtils.resetFloatingActionButtonMargin(mStartStopTimerButton);
        ViewUtils.resetFloatingActionButtonMargin(mRestartTimerButton);

        onCreateView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        mPresenter.onSaveInstanceState();

        Bundle state = new Bundle();

        state.putParcelable(Constants.INSTANCE.getSUPER_STATE_KEY(), super.onSaveInstanceState());
        state.putSerializable(Constants.INSTANCE.getPRESENTER_KEY(), mPresenter);

        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mPresenter.onDestroyView();
        mPresenter = null;

        if (state instanceof Bundle) {
            mPresenter = (TimerPresenter) ((Bundle) state).getSerializable(Constants.INSTANCE.getPRESENTER_KEY());

            super.onRestoreInstanceState(((Bundle) state).getParcelable(Constants.INSTANCE.getSUPER_STATE_KEY()));

            mPresenter.onRestoreInstanceState(this);
        }
    }

    public void onCreate() {
        mPresenter = new TimerPresenter();
    }

    public void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public void showPreviousExerciseButton() {
        mPrevExerciseButton.setVisibility(View.VISIBLE);
    }

    public void hidePreviousExerciseButton() {
        mPrevExerciseButton.setVisibility(View.INVISIBLE);
    }

    public void showNextExerciseButton() {
        mNextExerciseButton.setVisibility(View.VISIBLE);
    }

    public void hideNextExerciseButton() {
        mNextExerciseButton.setVisibility(View.INVISIBLE);
    }

    public void setMinutes(String text) {
        mTimerMinutesTextView.setText(text);
    }

    public void setSeconds(String text) {
        mTimerSecondsTextView.setText(text);
    }

    public void showPlaying() {
        mStartStopTimerButton.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_pause)
        );
    }

    public void showPaused() {
        mStartStopTimerButton.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_play)
        );
    }

    @OnClick(R.id.prev_exercise_button)
    public void onClickPreviousExerciseButton() {
        mPresenter.onClickPreviousExerciseButton();
    }

    @OnClick(R.id.next_exercise_button)
    public void onClickNextExerciseButton() {
        mPresenter.onClickNextExerciseButton();
    }

    @OnClick(R.id.timer_layout)
    public void onClickTimeLayout() {
        mPresenter.onClickTimeLayout();
    }

    @OnClick(R.id.increase_timer_button)
    public void onClickIncreaseTimeButton() {
        mPresenter.onClickIncreaseTimeButton();
    }

    @OnClick(R.id.start_stop_timer_button)
    public void onClickStartStopTimeButton() {
        mPresenter.onClickStartStopTimeButton();
    }

    @OnClick(R.id.restart_timer_button)
    public void onClickRestartTimeButton() {
        mPresenter.onClickRestartTimeButton();
    }
}
