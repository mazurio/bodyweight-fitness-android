package io.mazur.fit.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.mazur.fit.R;
import io.mazur.fit.presenter.ActionPresenter;
import io.mazur.fit.view.widget.Fab;

public class ActionView extends RelativeLayout {
    private ActionPresenter mActionPresenter;

    private MaterialSheetFab mMaterialSheet;

    @InjectView(R.id.action_view_overlay) View mActionViewOverlay;
    @InjectView(R.id.action_view_log_workout_button) Fab mActionViewLogWorkoutButton;
    @InjectView(R.id.action_view_action_button) Fab mActionViewActionButton;
    @InjectView(R.id.action_view_action_sheet) CardView mActionViewActionSheet;
    @InjectView(R.id.action_view_action_sheet_choose_progression) TextView mActionViewActionSheetChooseProgression;

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

    public void onCreate() {
        mActionPresenter = new ActionPresenter();
    }

    public void onCreateView() {
        mActionPresenter.onCreateView(this);
    }

    public void setActionButtonImageDrawable(int drawable) {
        mActionViewActionButton.setImageDrawable(getResources().getDrawable(drawable));
    }

    public void showActionSheetChooseProgression() {
        mActionViewActionSheetChooseProgression.setVisibility(View.VISIBLE);
    }

    public void hideActionSheetChooseProgression() {
        mActionViewActionSheetChooseProgression.setVisibility(View.GONE);
    }

    @OnClick(R.id.action_view_log_workout_button)
    public void onClickLogWorkoutButton(View view) {
        mActionPresenter.onClickLogWorkoutButton();
    }

    @OnClick(R.id.action_view_action_sheet_buy_equipment)
    public void onClickBuyEquipment(View view) {
        mMaterialSheet.hideSheet();
        mActionPresenter.onClickBuyEquipment();
    }

    @OnClick(R.id.action_view_action_sheet_watch_on_youtube)
    public void onClickWatchOnYouTube(View view) {
        mMaterialSheet.hideSheet();
        mActionPresenter.onClickWatchOnYouTube();
    }

    @OnClick(R.id.action_view_action_sheet_choose_progression)
    public void onClickChooseProgression(View view) {
        mMaterialSheet.hideSheet();
        mActionPresenter.onClickChooseProgression();
    }

    @OnClick(R.id.action_view_action_sheet_log_workout)
    public void onClickLogWorkout(View view) {
        mMaterialSheet.hideSheet();
        mActionPresenter.onClickLogWorkout();
    }
}
