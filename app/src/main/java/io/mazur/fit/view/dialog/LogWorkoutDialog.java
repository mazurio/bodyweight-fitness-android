package io.mazur.fit.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;

public class LogWorkoutDialog {
    private Context mContext;
    private Dialog mDialog;

    private int mNumberOfSets = 0;

    private ArrayList<LinearLayout> mSetLayouts = new ArrayList<>();

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.layout) LinearLayout mMainLayout;

    public LogWorkoutDialog(Context context) {
        mContext = context;

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_log_workout);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        ButterKnife.inject(this, mDialog);

        mToolbar.inflateMenu(R.menu.menu_log_workout);
        mToolbar.setTitle("Wall Extensions");
        mToolbar.setSubtitle("Monday, 26 October");
        mToolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_set:
                    addSet();

                    return true;

                case R.id.action_remove_set:
                    removeSet();

                    return true;
            }

            return false;
        });

        addSet();

        mDialog.show();
    }

    public void addSet() {
        if(mNumberOfSets >= 12) {
            return;
        }

        LinearLayout setLayout = null;

        if(mNumberOfSets == 0 || (mNumberOfSets % 4) == 0) {
            setLayout = (LinearLayout) LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.view_dialog_log_workout_row, mMainLayout, false);

            mMainLayout.addView(setLayout);
            mSetLayouts.add(setLayout);
        } else {
            setLayout = mSetLayouts.get(mSetLayouts.size() - 1);
        }

        if(setLayout != null) {
            CardView setView = (CardView) LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.view_dialog_log_workout_set, setLayout, false);

            setLayout.addView(setView);

            mNumberOfSets += 1;
        }
    }

    public void removeSet() {
        if(mNumberOfSets == 1) {
            return;
        }

        LinearLayout setLayout = mSetLayouts.get(mSetLayouts.size() - 1);

        if(setLayout != null) {
            setLayout.removeViewAt(setLayout.getChildCount() - 1);

            mNumberOfSets -= 1;

            if(setLayout.getChildCount() == 0) {
                mMainLayout.removeView(setLayout);
                mSetLayouts.remove(setLayout);
            }
        }
    }
}
