package com.bodyweight.fitness.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bodyweight.fitness.Constants;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.presenter.ActionPresenter;
import com.bodyweight.fitness.utils.ViewUtils;
import com.bodyweight.fitness.view.widget.ActionButton;

public class ActionView extends CoordinatorLayout {
    ActionPresenter mPresenter;

    MaterialSheetFab mMaterialSheet;

    @InjectView(R.id.action_view_overlay)
    View mActionViewOverlay;

    @InjectView(R.id.action_view_log_workout_button)
    ActionButton mActionViewLogWorkoutButton;

    @InjectView(R.id.action_view_action_button)
    ActionButton mActionViewActionButton;

    @InjectView(R.id.action_view_action_sheet)
    CardView mActionViewActionSheet;

    @InjectView(R.id.action_view_action_sheet_choose_progression)
    TextView mActionViewActionSheetChooseProgression;

    public ActionView(Context context) {
        super(context);

        onCreate();
    }

    public ActionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        onCreate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        ViewUtils.resetFloatingActionButtonMargin(mActionViewLogWorkoutButton);
        ViewUtils.resetFloatingActionButtonMargin(
                mActionViewActionButton,
                0,
                0,
                12,
                12
        );

        mMaterialSheet = new MaterialSheetFab<>(
                mActionViewActionButton,
                mActionViewActionSheet,
                mActionViewOverlay,
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#FFFDDD")
        );

        mMaterialSheet.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();

                mActionViewLogWorkoutButton.hide();
            }

            @Override
            public void onSheetShown() {
                super.onSheetShown();
            }

            @Override
            public void onHideSheet() {
                super.onHideSheet();
            }

            @Override
            public void onSheetHidden() {
                super.onSheetHidden();

                mActionViewLogWorkoutButton.show();
            }
        });

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
            mPresenter = (ActionPresenter) ((Bundle) state).getSerializable(Constants.INSTANCE.getPRESENTER_KEY());

            super.onRestoreInstanceState(((Bundle) state).getParcelable(Constants.INSTANCE.getSUPER_STATE_KEY()));

            mPresenter.onRestoreInstanceState(this);
        }
    }

    public void onCreate() {
        mPresenter = new ActionPresenter();
    }

    public void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public void setActionButtonImageDrawable(int drawable) {
        mActionViewActionButton.setImageDrawable(getResources().getDrawable(drawable));
    }

    public void showActionButtons() {
        mActionViewLogWorkoutButton.setVisibility(View.VISIBLE);
        mActionViewActionButton.setVisibility(View.VISIBLE);
    }

    public void hideActionButtons() {
        mActionViewLogWorkoutButton.setVisibility(View.GONE);
        mActionViewActionButton.setVisibility(View.GONE);
    }

    public void showActionSheetChooseProgression() {
        mActionViewActionSheetChooseProgression.setVisibility(View.VISIBLE);
    }

    public void hideActionSheetChooseProgression() {
        mActionViewActionSheetChooseProgression.setVisibility(View.GONE);
    }

    @OnClick(R.id.action_view_log_workout_button)
    @SuppressWarnings("unused")
    public void onClickLogWorkoutButton(View view) {
        mPresenter.onClickLogWorkoutButton();
    }

    @OnClick(R.id.action_view_action_sheet_watch_on_youtube)
    @SuppressWarnings("unused")
    public void onClickWatchOnYouTube(View view) {
        mMaterialSheet.hideSheet();
        mPresenter.onClickWatchOnYouTube();
    }

    @OnClick(R.id.action_view_action_sheet_choose_progression)
    @SuppressWarnings("unused")
    public void onClickChooseProgression(View view) {
        mMaterialSheet.hideSheet();
        mPresenter.onClickChooseProgression();
    }

    @OnClick(R.id.action_view_action_sheet_log_workout)
    @SuppressWarnings("unused")
    public void onClickLogWorkout(View view) {
        mMaterialSheet.hideSheet();
        mPresenter.onClickLogWorkout();
    }
}
