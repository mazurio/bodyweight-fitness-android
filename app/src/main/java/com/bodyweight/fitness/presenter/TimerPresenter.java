package com.bodyweight.fitness.presenter;

import android.app.TimePickerDialog;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import com.bodyweight.fitness.Constants;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.stream.Stream;
import com.bodyweight.fitness.utils.PreferenceUtils;
import com.bodyweight.fitness.view.TimerView;

import java.util.UUID;

import io.realm.Realm;

public class TimerPresenter extends IPresenter<TimerView> {
    private static final int DEFAULT_SECONDS = 60;

    private transient Exercise mExercise;

    private int mSeconds = DEFAULT_SECONDS;
    private int mCurrentSeconds = mSeconds;
    private int mStartedLoggingSeconds = mSeconds;
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

        subscribe(RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
            mExercise = exercise;

            hideOrShowExerciseButtons();

            if (mRestored) {
                mRestored = false;

                restartTimer(mCurrentSeconds, true, mPlaying);

                if (mPlaying) {
                    startTimer();
                }
            } else {
                restartTimer(getSeconds(), false, false);
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
            mCountDownTimer = buildCountDownTimer(mCurrentSeconds, false, true);

            mView.setMinutes(formatMinutes(mCurrentSeconds));
            mView.setSeconds(formatSeconds(mCurrentSeconds));

            mCountDownTimer.start();
        } else {
            mCurrentSeconds += extraSeconds;
            mCountDownTimer = buildCountDownTimer(mCurrentSeconds, false, true);

            mView.setMinutes(formatMinutes(mCurrentSeconds));
            mView.setSeconds(formatSeconds(mCurrentSeconds));
        }
    }

    public void pauseTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mPlaying = false;
        mCountDownTimer = buildCountDownTimer(mCurrentSeconds, false, false);

        mView.setMinutes(formatMinutes(mCurrentSeconds));
        mView.setSeconds(formatSeconds(mCurrentSeconds));

        mView.showPaused();
    }

    public void startTimer() {
        mCountDownTimer.start();
    }

    public void restartTimer(int seconds, boolean restored, boolean isPlaying) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mPlaying = isPlaying;
        mCurrentSeconds = seconds;

        mCountDownTimer = buildCountDownTimer(seconds, restored, false);

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

            restartTimer(mCurrentSeconds, false, false);

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

        restartTimer(getSeconds(), false, false);
    }

    public int getSeconds() {
        return (int) PreferenceUtils.getInstance().getTimerValueForExercise(mExercise.getId(), DEFAULT_SECONDS * 1000) / 1000;
    }

    public CountDownTimer buildCountDownTimer(int seconds, boolean restored, boolean increaseTimer) {
        if (increaseTimer) {
            mLoggedSeconds += 5;
        } else {
            if (restored) {
                mLoggedSeconds = mStartedLoggingSeconds;
            } else {
                mStartedLoggingSeconds = seconds;
                mLoggedSeconds = seconds;
            }
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

                restartTimer(getSeconds(), false, false);

                playSound();
            }
        };
    }

    public void logTime() {
        if (PreferenceUtils.getInstance().automaticallyLogWorkoutTime() && mExercise.isTimedSet()) {
            int loggedSeconds = (mLoggedSeconds - mCurrentSeconds);

            if (loggedSeconds > 0) {
                if(logIntoRealm(loggedSeconds)) {
                    Stream.INSTANCE.setLoggedSeconds(loggedSeconds);
                }
            }
        }
    }

    private boolean logIntoRealm(int logSeconds) {
        // getRepositoryRoutineForToday method - begins realm transaction.
        RepositoryRoutine repositoryRoutine = RepositoryStream.getInstance().getRepositoryRoutineForToday();
        RepositoryExercise mRepositoryExercise = null;

        Realm realm = RepositoryStream.getInstance().getRealm();
        Exercise exercise = RoutineStream.getInstance().getExercise();

        realm.beginTransaction();
        for (RepositoryExercise repositoryExercise : repositoryRoutine.getExercises()) {
            if (repositoryExercise.getTitle().equals(exercise.getTitle())) {
                mRepositoryExercise = repositoryExercise;

                break;
            }
        }

        if (mRepositoryExercise != null) {
            // if there is already a set which is timed and has 0 seconds then overwrite the values.
            // otherwise create new one.

            int numberOfSets = mRepositoryExercise.getSets().size();

            if(numberOfSets >= Constants.MAXIMUM_NUMBER_OF_SETS) {
                realm.cancelTransaction();

                return false;
            }

            if (numberOfSets == 1 && mRepositoryExercise.getSets().get(0).isTimed() && mRepositoryExercise.getSets().get(0).getSeconds() == 0) {
                RepositorySet firstSet = mRepositoryExercise.getSets().get(0);

                if (firstSet.isTimed() && firstSet.getSeconds() == 0) {
                    firstSet.setSeconds(logSeconds);
                }
            } else {
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

            return true;
        } else {
            realm.cancelTransaction();

            return false;
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
