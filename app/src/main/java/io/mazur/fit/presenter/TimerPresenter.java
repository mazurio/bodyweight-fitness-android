package io.mazur.fit.presenter;

import android.app.TimePickerDialog;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

import io.mazur.fit.R;
import io.mazur.fit.model.DialogState;
import io.mazur.fit.model.TimerState;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.PreferenceUtil;
import io.mazur.fit.view.TimerView;

/**
 * TODO: Refactor.
 * TODO: Unit Test this.
 * TODO: Basically everything here :).
 */
public class TimerPresenter implements Serializable {
    private static final long ONE_MINUTE = 60000;
    private static final long ONE_SECOND = 1000;

    private transient TimerView mTimerView;
    private transient CountDownTimer mCountDownTimer;

    private long mTimeInMillis = PreferenceUtil.getInstance().getTimerValue(ONE_MINUTE);
    private DateTime mTime = new DateTime(mTimeInMillis);

    private TimerState mTimerState = TimerState.PAUSED;
    private DialogState mDialogState = DialogState.HIDDEN;

    private transient TimePickerDialog mTimePickerDialog;

    public void onCreateView(TimerView timerView) {
        mTimerView = timerView;

//        MediaPlayer p = MediaPlayer.create(mTimerView.getContext(), R.raw.timer);
//        p.setLooping(true);
//        p.start();

        RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
            if(exercise.isPrevious()) {
                mTimerView.getPrevExerciseButton().setVisibility(View.VISIBLE);
                mTimerView.getPrevExerciseButton().setOnClickListener(v -> {
                    RoutineStream.getInstance().setExercise(exercise.getPrevious());
                });
            } else {
                mTimerView.getPrevExerciseButton().setVisibility(View.INVISIBLE);
            }

            if(exercise.isNext()) {
                mTimerView.getNextExerciseButton().setVisibility(View.VISIBLE);
                mTimerView.getNextExerciseButton().setOnClickListener(v -> {
                    RoutineStream.getInstance().setExercise(exercise.getNext());
                });
            } else {
                mTimerView.getNextExerciseButton().setVisibility(View.INVISIBLE);
            }
        });

        mTimePickerDialog = new TimePickerDialog(mTimerView.getContext(), (view, hourOfDay, minute) -> {
            mDialogState = DialogState.HIDDEN;

            long minutes = ONE_MINUTE * hourOfDay;
            long seconds = ONE_SECOND * minute;

            mTimerState = TimerState.PAUSED;
            mTimeInMillis = minutes + seconds;
            mTime = new DateTime(mTimeInMillis);

            PreferenceUtil.getInstance().setTimerValue(mTimeInMillis);

            mTimerView.getStartStopTimerButton().setImageDrawable(
                    mTimerView.getResources().getDrawable(R.drawable.ic_play)
            );

            mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
            mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));

            if(mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }

            // set those (chosen values) in prefences using PreferenceUtil.getInstance()
        }, mTime.getMinuteOfHour(), mTime.getSecondOfMinute(), true);

        mTimePickerDialog.setOnCancelListener(d -> mDialogState = DialogState.HIDDEN);

        RoutineStream.getInstance().getExerciseChangedObservable().subscribe(exercise -> {
            restartTimer();
        });

        if(mDialogState == DialogState.SHOWN) {
            mTimePickerDialog.show();
        }

        mTimerView.getTimerLayout().setOnClickListener(v -> {
            mDialogState = DialogState.SHOWN;

            mTimePickerDialog.updateTime(mTime.getMinuteOfHour(), mTime.getSecondOfMinute());
            mTimePickerDialog.show();
        });

        mTimerView.getIncreaseTimerButton().setOnClickListener(v -> {
            switch(mTimerState) {
                case PAUSED: {
                    mTimerState = TimerState.PAUSED;
                    mTimeInMillis = mTimeInMillis + (ONE_SECOND * 5);
                    mTime = new DateTime(mTimeInMillis);

                    mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
                    mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));

                    if(mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }

                    break;
                }

                case STARTED: {
                    mTimeInMillis = mTimeInMillis + (ONE_SECOND * 5);
                    mTime = new DateTime(mTimeInMillis);

                    mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
                    mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));

                    if(mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }

                    createCountDownTimer();

                    if(mCountDownTimer != null) {
                        mCountDownTimer.start();
                    }

                    break;
                }
            }
        });

        mTimerView.getStartStopTimerButton().setOnClickListener(v -> {
            switch(mTimerState) {
                case PAUSED: {
                    mTimerState = TimerState.STARTED;

                    mTimerView.getStartStopTimerButton().setImageDrawable(
                            mTimerView.getResources().getDrawable(R.drawable.ic_pause)
                    );

                    createCountDownTimer();

                    if(mCountDownTimer != null) {
                        mCountDownTimer.start();
                    }

                    break;
                }

                case STARTED: {
                    mTimerState = TimerState.PAUSED;

                    mTimerView.getStartStopTimerButton().setImageDrawable(
                            mTimerView.getResources().getDrawable(R.drawable.ic_play)
                    );

                    mTimeInMillis = mTime.getMillis();

                    if(mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }

                    break;
                }
            }
        });

        mTimerView.getRestartTimerButton().setOnClickListener(v -> restartTimer());

        switch(mTimerState) {
            case PAUSED: {
                mTimerView.getStartStopTimerButton().setImageDrawable(
                        mTimerView.getResources().getDrawable(R.drawable.ic_play)
                );

                mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
                mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));

                break;
            }

            case STARTED: {
                mTimeInMillis = mTime.getMillis();

                mTimerView.getStartStopTimerButton().setImageDrawable(
                        mTimerView.getResources().getDrawable(R.drawable.ic_pause)
                );

                createCountDownTimer();

                if(mCountDownTimer != null) {
                    mCountDownTimer.start();
                }

                break;
            }
        }
    }

    public void onDestroyView() {
        if(mTimePickerDialog != null && mTimePickerDialog.isShowing()) {
            mTimePickerDialog.dismiss();
        }

        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public void createCountDownTimer() {
        mCountDownTimer = new CountDownTimer(mTimeInMillis, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTime = new DateTime(millisUntilFinished);
                mTimeInMillis = mTime.getMillis();

                mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
                mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));
            }

            @Override
            public void onFinish() {
                mTimerState = TimerState.PAUSED;

                mTimeInMillis = PreferenceUtil.getInstance().getTimerValue(ONE_MINUTE);
                mTime = new DateTime(mTimeInMillis);

                mTimerView.getStartStopTimerButton().setImageDrawable(
                        mTimerView.getResources().getDrawable(R.drawable.ic_play)
                );

                mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
                mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));
            }
        };
    }

    public void restartTimer() {
        mTimerState = TimerState.PAUSED;

        mTimeInMillis = PreferenceUtil.getInstance().getTimerValue(ONE_MINUTE);
        mTime = new DateTime(mTimeInMillis);

        mTimerView.getStartStopTimerButton().setImageDrawable(
                mTimerView.getResources().getDrawable(R.drawable.ic_play)
        );

        mTimerView.getTimerMinutesTextView().setText(DateTimeFormat.forPattern("mm").print(mTime));
        mTimerView.getTimerSecondsTextView().setText(DateTimeFormat.forPattern("ss").print(mTime));

        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
}
