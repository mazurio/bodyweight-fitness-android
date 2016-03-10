package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

import icepick.Icepick;
import icepick.State;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.presenter.ContentPresenter;

public class ContentView extends RelativeLayout {
    @State
    ContentPresenter mPresenter;

    @InjectView(R.id.view_home)
    View mHomeView;

    @InjectView(R.id.view_change_routine)
    ChangeRoutineView mChangeRoutineView;

    @InjectView(R.id.view_calendar)
    CalendarView mCalendarView;

    public ContentView(Context context) {
        super(context);

        onCreate();
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        onCreate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        onCreateView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        mPresenter.onSaveInstanceState();

        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mPresenter.onDestroyView();
        mPresenter = null;

        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));

        mPresenter.onRestoreInstanceState(this);
    }

    private void onCreate() {
        mPresenter = new ContentPresenter();
    }

    private void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public void showHome() {
        mHomeView.setVisibility(View.VISIBLE);
        mChangeRoutineView.setVisibility(View.GONE);
        mCalendarView.setVisibility(View.GONE);
    }

    public void showChangeRoutine() {
        mHomeView.setVisibility(View.GONE);
        mChangeRoutineView.setVisibility(View.VISIBLE);
        mCalendarView.setVisibility(View.GONE);
    }

    public void showCalendar() {
        mHomeView.setVisibility(View.GONE);
        mChangeRoutineView.setVisibility(View.GONE);
        mCalendarView.setVisibility(View.VISIBLE);
    }
}
