package io.mazur.fit.view;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import icepick.Icepick;
import icepick.State;

import io.mazur.fit.R;
import io.mazur.fit.presenter.TimerPresenter;

public class TimerView extends LinearLayout {
    @State
    TimerPresenter mTimerPresenter;

    @InjectView(R.id.timer_layout)
    RelativeLayout mTimerLayout;

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

        resetFloatingActionButtonMargin(getIncreaseTimerButton());
        resetFloatingActionButtonMargin(getStartStopTimerButton());
        resetFloatingActionButtonMargin(getRestartTimerButton());

        onCreateView();
    }

    public void onCreate() {
        mTimerPresenter = new TimerPresenter();
    }

    public void onCreateView() {
        mTimerPresenter.onCreateView(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        mTimerPresenter.onDestroyView();

        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));

        onCreateView();
    }

    private void resetFloatingActionButtonMargin(FloatingActionButton floatingActionButton) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            floatingActionButton.setLayoutParams(layoutParams);
        }
    }

    public RelativeLayout getTimerLayout() {
        return mTimerLayout;
    }

    public ImageButton getPrevExerciseButton() {
        return mPrevExerciseButton;
    }

    public ImageButton getNextExerciseButton() {
        return mNextExerciseButton;
    }

    public TextView getTimerMinutesTextView() {
        return mTimerMinutesTextView;
    }

    public TextView getTimerSecondsTextView() {
        return mTimerSecondsTextView;
    }

    public FloatingActionButton getIncreaseTimerButton() {
        return mIncreaseTimerButton;
    }

    public FloatingActionButton getStartStopTimerButton() {
        return mStartStopTimerButton;
    }

    public FloatingActionButton getRestartTimerButton() {
        return mRestartTimerButton;
    }
}
