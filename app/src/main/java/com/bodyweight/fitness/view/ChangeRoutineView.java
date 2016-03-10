package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.presenter.ChangeRoutinePresenter;

import butterknife.ButterKnife;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class ChangeRoutineView extends RelativeLayout {
    @State
    ChangeRoutinePresenter mPresenter;

    @InjectView(R.id.routine1progress)
    public SeekBar mRoutine1ProgressBar;

    public ChangeRoutineView(Context context) {
        super(context);

        onCreate();
    }

    public ChangeRoutineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    public ChangeRoutineView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mPresenter = new ChangeRoutinePresenter();
    }

    private void onCreateView() {
        mPresenter.onCreateView(this);

        mRoutine1ProgressBar.setEnabled(false);
    }

    @OnClick(R.id.remove_cache)
    public void onClickRemoveCache(View view) {
        mPresenter.onClickRemoveCache();
    }

    @OnClick(R.id.routine0)
    public void onClickRoutine0(View view) {
        mPresenter.onClickRoutine0();
    }

    @OnClick(R.id.routine1)
    public void onClickRoutine1(View view) {
        mPresenter.onClickRoutine1();
    }

    @OnClick(R.id.routine1download)
    public void onClickRoutine1Download(View view) {
        mPresenter.onClickRoutine1Download();
    }
}
