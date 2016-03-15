package com.bodyweight.fitness.presenter;

import android.app.TimePickerDialog;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import com.bodyweight.fitness.model.Exercise;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.PreferenceUtils;
import com.bodyweight.fitness.view.TimerView;

import rx.Observable;

public class TimerPresenter extends IPresenter<TimerView> {
    private transient Exercise mExercise;

    private int mSeconds = 60;
    private int mCurrentSeconds = mSeconds;

    private boolean mPlaying = false;
    private boolean mRestored = false;

    private transient CountDownTimer mCountDownTimer;

    @Override
    public void onCreateView(TimerView view) {
        super.onCreateView(view);

        restartTimer(getSeconds(), false);
    }

    @Override
    public void onSaveInstanceState() {
        mRestored = true;

        super.onSaveInstanceState();
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(Observable.merge(
                RoutineStream.getInstance().getExerciseObservable(),
                RoutineStream.getInstance().getExerciseChangedObservable()
        ).subscribe(exercise -> {
            mExercise = exercise;

            hideOrShowExerciseButtons();

            if (mRestored) {
                mRestored = false;

                restartTimer(mCurrentSeconds, mPlaying);

                startTimer();
            } else {
                restartTimer(getSeconds(), false);
            }
        }));
    }

    private void hideOrShowExerciseButtons() {
        if (mExercise.isPrevious()) {
            mView.showPreviousExerciseButton();
        } else {
            mView.hidePreviousExerciseButton();
        }

        if (mExercise.isNext()) {
            mView.showNextExerciseButton();
        } else {
            mView.hideNextExerciseButton();
        }
    }

    public void increaseTimer(int extraSeconds) {
        if (mPlaying) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }

            mCurrentSeconds += extraSeconds;
            mCountDownTimer = buildCountDownTimer(mCurrentSeconds);

            mView.setMinutes(formatMinutes(mCurrentSeconds));
            mView.setSeconds(formatSeconds(mCurrentSeconds));

            mCountDownTimer.start();
        } else {
            mCurrentSeconds += extraSeconds;
            mCountDownTimer = buildCountDownTimer(mCurrentSeconds);

            mView.setMinutes(formatMinutes(mCurrentSeconds));
            mView.setSeconds(formatSeconds(mCurrentSeconds));
        }
    }

    public void pauseTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mPlaying = false;
        mCountDownTimer = buildCountDownTimer(mCurrentSeconds);

        mView.setMinutes(formatMinutes(mCurrentSeconds));
        mView.setSeconds(formatSeconds(mCurrentSeconds));

        mView.showPaused();
    }

    public void startTimer() {
        mCountDownTimer.start();
    }

    public void restartTimer(int seconds, boolean isPlaying) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mPlaying = isPlaying;
        mCurrentSeconds = seconds;

        mCountDownTimer = buildCountDownTimer(seconds);

        mView.setMinutes(formatMinutes(seconds));
        mView.setSeconds(formatSeconds(seconds));

        mView.showPaused();
    }

    public void playSound() {
        if(PreferenceUtils.getInstance().playSoundWhenTimerStops()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.finished);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
    }

    public void onClickPreviousExerciseButton() {
        if (mExercise.isPrevious()) {
            RoutineStream.getInstance().setExercise(mExercise.getPrevious());
        }
    }

    public void onClickNextExerciseButton() {
        if (mExercise.isNext()) {
            RoutineStream.getInstance().setExercise(mExercise.getNext());
        }
    }

    public void onClickTimeLayout() {
        pauseTimer();

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, minutes, seconds) -> {
            mCurrentSeconds = seconds + (minutes * 60);

            if (mCurrentSeconds < 10) {
                mCurrentSeconds = 10;
            }

            restartTimer(mCurrentSeconds, false);

            mSeconds = mCurrentSeconds;

            PreferenceUtils.getInstance().setTimerValue(mSeconds * 1000);
        }, formatMinutesAsNumber(mCurrentSeconds), formatSecondsAsNumber(mCurrentSeconds), true);

        timePickerDialog.show();
    }

    public void onClickIncreaseTimeButton() {
        increaseTimer(5);
    }

    public void onClickStartStopTimeButton() {
        if (mPlaying) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    public void onClickRestartTimeButton() {
        restartTimer(getSeconds(), false);
    }

    public int getSeconds() {
        return (int) PreferenceUtils.getInstance().getTimerValue(mSeconds * 1000) / 1000;
    }

    public CountDownTimer buildCountDownTimer(int seconds) {
        return new CountDownTimer(seconds * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) millisUntilFinished / 1000;

                mPlaying = true;
                mCurrentSeconds = seconds;

                mView.setMinutes(formatMinutes(seconds));
                mView.setSeconds(formatSeconds(seconds));

                mView.showPlaying();
            }

            @Override
            public void onFinish() {
                restartTimer(getSeconds(), false);

                playSound();
            }
        };
    }

    public String formatMinutes(int seconds) {
        int minutes = seconds / 60;

        if (minutes == 0) {
            return "00";
        } else if (minutes < 10) {
            return "0" + minutes;
        }

        return String.valueOf(minutes);
    }

    public int formatMinutesAsNumber(int seconds) {
        return seconds / 60;
    }

    public String formatSeconds(int seconds) {
        int s = seconds % 60;

        if (s == 0) {
            return "00";
        } else if (s < 10) {
            return "0" + s;
        }

        return String.valueOf(s);
    }

    public int formatSecondsAsNumber(int seconds) {
        return seconds % 60;
    }
}
