package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.ChangeRoutineListAdapter;
import com.bodyweight.fitness.presenter.ChangeRoutinePresenter;

import butterknife.ButterKnife;

import butterknife.InjectView;

public class ChangeRoutineView extends RelativeLayout {
    ChangeRoutinePresenter mPresenter;

    @InjectView(R.id.view_change_routine_list)
    RecyclerView mList;

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

        mList.setLayoutManager(new LinearLayoutManager(getContext()));

        onCreateView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        mPresenter.onSaveInstanceState();

        Bundle state = new Bundle();

        state.putParcelable(Constants.SUPER_STATE_KEY, super.onSaveInstanceState());
        state.putSerializable(Constants.PRESENTER_KEY, mPresenter);

        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mPresenter.onDestroyView();
        mPresenter = null;

        if (state instanceof Bundle) {
            mPresenter = (ChangeRoutinePresenter) ((Bundle) state).getSerializable(Constants.PRESENTER_KEY);

            super.onRestoreInstanceState(((Bundle) state).getParcelable(Constants.SUPER_STATE_KEY));

            mPresenter.onRestoreInstanceState(this);
        }
    }

    private void onCreate() {
        mPresenter = new ChangeRoutinePresenter();
    }

    private void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public void setListAdapter(ChangeRoutineListAdapter changeRoutineListAdapter) {
        mList.setAdapter(changeRoutineListAdapter);
    }
}
