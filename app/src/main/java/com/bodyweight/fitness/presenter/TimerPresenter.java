package com.bodyweight.fitness.presenter;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;

import com.bodyweight.fitness.model.Exercise;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;
import com.bodyweight.fitness.utils.PreferenceUtils;
import com.bodyweight.fitness.view.TimerView;

import java.util.UUID;

import io.realm.Realm;
import rx.Observable;

public class TimerPresenter extends IPresenter<TimerView> {
    private transient Exercise mExercise;

    private int mSeconds = 60;
    private int mCurrentSeconds = mSeconds;
    private int mLoggedSeconds = 0;

    private boolean mPlaying = false;
    private boolean mRestored = false;

    private transient CountDownTimer mCountDownTimer;

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
            mCountDownTimer = buildCountDownTimer(mCurrentSeconds, true);

            mView.setMinutes(formatMinutes(mCurrentSeconds));
            mView.setSeconds(formatSeconds(mCurrentSeconds));

            mCountDownTimer.start();
        } else {
            mCurrentSeconds += extraSeconds;
            mCountDownTimer = buildCountDownTimer(mCurrentSeconds, true);

            mView.setMinutes(formatMinutes(mCurrentSeconds));
            mView.setSeconds(formatSeconds(mCurrentSeconds));
        }
    }

    public void pauseTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mPlaying = false;
        mCountDownTimer = buildCountDownTimer(mCurrentSeconds, false);

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

        mCountDownTimer = buildCountDownTimer(seconds, false);

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

            PreferenceUtils.getInstance().setTimerValue(mExercise.getId(), mSeconds * 1000);
        }, formatMinutesAsNumber(mCurrentSeconds), formatSecondsAsNumber(mCurrentSeconds), true);

        timePickerDialog.show();
    }

    public void onClickIncreaseTimeButton() {
        increaseTimer(5);
    }

    public void onClickStartStopTimeButton() {
        if (mPlaying) {
            logTime();

            pauseTimer();
        } else {
            startTimer();
        }
    }

    public void onClickRestartTimeButton() {
        logTime();

        restartTimer(getSeconds(), false);
    }

    public int getSeconds() {
        return (int) PreferenceUtils.getInstance().getTimerValueForExercise(mExercise.getId(), mSeconds * 1000) / 1000;
    }

    public CountDownTimer buildCountDownTimer(int seconds, boolean increaseTimer) {
        if (increaseTimer) {
            mLoggedSeconds += 5;
        } else {
            mLoggedSeconds = seconds;
        }

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
                logTime();

                restartTimer(getSeconds(), false);

                playSound();
            }
        };
    }

    public void logTime() {
        if (PreferenceUtils.getInstance().automaticallyLogWorkoutTime() && mExercise.isTimedSet()) {
            int logSeconds = (mLoggedSeconds - mCurrentSeconds);

            if (logSeconds <= 0) {
                Logger.d("Nothing to log as <= 0");
            } else {
                Snackbar.make(mView, String.format("Logging time %s:%s", formatMinutes(logSeconds), formatSeconds(logSeconds)), Snackbar.LENGTH_LONG)
                        .setAction("CANCEL", (view) -> {
                            Logger.d("CANCEL");
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);

                                // TODO: Log Workout Dialog crashes when shown and transaction is being made in the background.

                                Logger.d("onDismissed");

                                Realm realm = RepositoryStream.getInstance().getRealm();

                                Exercise exercise = RoutineStream.getInstance().getExercise();

                                realm.beginTransaction();

                                RepositoryRoutine repositoryRoutine = RepositoryStream.getInstance().getRepositoryRoutineForToday();
                                RepositoryExercise mRepositoryExercise = null;

                                for (RepositoryExercise repositoryExercise : repositoryRoutine.getExercises()) {
                                    if (repositoryExercise.getTitle().equals(exercise.getTitle())) {
                                        mRepositoryExercise = repositoryExercise;

                                        break;
                                    }
                                }

                                if (mRepositoryExercise != null) {
                                    // if there is already a set which is timed and has 0 seconds then overwrite the values.
                                    // otherwise create new one.

                                    if (mRepositoryExercise.getSets().size() == 1) {
                                        Logger.d("Modify first set");

                                        RepositorySet firstSet = mRepositoryExercise.getSets().get(0);

                                        if (firstSet.isTimed() && firstSet.getSeconds() == 0) {
                                            firstSet.setSeconds(logSeconds);
                                        }
                                    } else {
                                        Logger.d("Add set");

                                        RepositorySet repositorySet = realm.createObject(RepositorySet.class);

                                        repositorySet.setId("Set-" + UUID.randomUUID().toString());
                                        repositorySet.setIsTimed(true);
                                        repositorySet.setSeconds(logSeconds);
                                        repositorySet.setWeight(0);
                                        repositorySet.setReps(0);

                                        repositorySet.setExercise(mRepositoryExercise);

                                        mRepositoryExercise.getSets().add(repositorySet);
                                    }

                                    realm.copyToRealmOrUpdate(mRepositoryExercise);
                                    realm.commitTransaction();
                                } else {
                                    Logger.d("Cancel transaction");

                                    realm.cancelTransaction();
                                }
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                            }
                        })
                        .setActionTextColor(Color.WHITE)
                        .show();
            }
        }
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
