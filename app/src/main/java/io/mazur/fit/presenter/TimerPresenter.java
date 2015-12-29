package io.mazur.fit.presenter;

import android.app.TimePickerDialog;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.PreferenceUtils;
import io.mazur.fit.view.TimerView;

import rx.Observable;

public class TimerPresenter extends IPresenter<TimerView> {
    private transient Exercise mExercise;

    private int mSeconds = 60;
    private int mCurrentSeconds = mSeconds;

    private boolean mPlaying = false;

    private transient CountDownTimer mCountDownTimer;

    @Override
    public void onCreateView(TimerView view) {
        super.onCreateView(view);

        restartTimer(getSeconds());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState() {
        super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(TimerView view) {
        super.onRestoreInstanceState(view);
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
            restartTimer(getSeconds());
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

    public void restartTimer(int seconds) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mPlaying = false;
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

            restartTimer(mCurrentSeconds);

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
        restartTimer(getSeconds());
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
                restartTimer(getSeconds());

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
