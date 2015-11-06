package io.mazur.fit.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import butterknife.OnClick;
import io.mazur.fit.R;
import io.mazur.fit.model.realm.Set;

public class TestDialog {
    private Dialog mDialog;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.saveButton) Button mSaveButton;

    @InjectView(R.id.setView) LinearLayout mSetView;
    @InjectView(R.id.actionView) View mActionView;
    @InjectView(R.id.setValue) TextView mActionViewSetValue;

    @InjectView(R.id.weightValue) TextView mActionViewWeightValue;
    @InjectView(R.id.weightDescription) TextView mActionViewWeightDescription;

    @InjectView(R.id.repsValue) TextView mActionViewRepsValue;
    @InjectView(R.id.repsDescription) TextView mActionViewRepsDescription;

    @InjectView(R.id.actionViewCloseButton) View mActionViewCloseButton;

    private ArrayList<View> mViewSets = new ArrayList<>();
    private ArrayList<Set> mSets = new ArrayList<>();

    private Set mSet;

    public TestDialog(Context context) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_test);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        ButterKnife.inject(this, mDialog);

        mActionView.setVisibility(View.GONE);

        mToolbar.setTitle("Title");
        mToolbar.setSubtitle("Subtitle");
        mToolbar.inflateMenu(R.menu.menu_log_workout);
        mToolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_set: {
                    Set set = new Set();
                    set.setIsTimed(false);

                    return addSet(set);
                }

                case R.id.action_add_timed_set: {
                    Set set = new Set();
                    set.setIsTimed(true);

                    return addTimedSet(set);
                }

                case R.id.action_remove_set: {
                    return true;
                }
            }

            return false;
        });

        mActionViewCloseButton.setOnClickListener(v -> {
            updateSets();

            mSetView.setVisibility(View.VISIBLE);
            mActionView.setVisibility(View.GONE);
        });

        mSaveButton.setOnClickListener(v -> {
            mDialog.dismiss();
        });

        mDialog.show();
    }

    public void updateSets() {
        int i = 0;
        for(View view : mViewSets) {
            Set set = mSets.get(i);

            if(set.isTimed()) {
                TextView minutes = (TextView) view.findViewById(R.id.minutesValue);
                TextView seconds = (TextView) view.findViewById(R.id.secondsValue);

                minutes.setText(formatMinutes(set.getSeconds()) + "m");
                seconds.setText(formatSeconds(set.getSeconds()) + "s");
            } else {
                TextView reps = (TextView) view.findViewById(R.id.repsValue);
                TextView weight = (TextView) view.findViewById(R.id.weightValue);

                reps.setText(formatReps(set.getReps()));
                weight.setText(formatWeight(set.getWeight()));
            }

            i++;
        }
    }

    public boolean addSet(Set set) {
        View view = LayoutInflater.from(mDialog.getContext())
                .inflate(R.layout.view_dialog_test_set, mSetView, false);

        TextView reps = (TextView) view.findViewById(R.id.repsValue);
        TextView weight = (TextView) view.findViewById(R.id.weightValue);

        reps.setText(formatReps(set.getReps()));
        weight.setText(formatWeight(set.getWeight()));

        view.setOnClickListener(v -> {
            updateActionView(set, mSets.size());

            mSetView.setVisibility(View.GONE);
            mActionView.setVisibility(View.VISIBLE);
        });

        mSetView.addView(view);

        mSets.add(set);
        mViewSets.add(view);

        return true;
    }

    public boolean addTimedSet(Set set) {
        View view = LayoutInflater.from(mDialog.getContext())
                .inflate(R.layout.view_dialog_test_timed_set, mSetView, false);

        TextView minutes = (TextView) view.findViewById(R.id.minutesValue);
        TextView seconds = (TextView) view.findViewById(R.id.secondsValue);

        minutes.setText(formatMinutes(set.getSeconds()) + "m");
        seconds.setText(formatSeconds(set.getSeconds()) + "s");

        view.setOnClickListener(v -> {
            updateActionViewForTimedSet(set, mSets.size());

            mSetView.setVisibility(View.GONE);
            mActionView.setVisibility(View.VISIBLE);
        });

        mSetView.addView(view);

        mSets.add(set);
        mViewSets.add(view);

        return true;
    }

    public void updateActionView(Set set, int index) {
        mSet = set;

        mActionViewSetValue.setText(String.valueOf(index));

        mActionViewWeightValue.setText(String.valueOf(set.getWeight()));
        mActionViewWeightDescription.setText("Weight (kg)");

        mActionViewRepsValue.setText(String.valueOf(set.getReps()));
        mActionViewRepsDescription.setText("Reps");
    }

    public void updateActionViewForTimedSet(Set set, int index) {
        mSet = set;

        mActionViewSetValue.setText(String.valueOf(index));

        mActionViewWeightValue.setText(formatMinutes(mSet.getSeconds()));
        mActionViewWeightDescription.setText("Minutes");

        mActionViewRepsValue.setText(formatSeconds(mSet.getSeconds()));
        mActionViewRepsDescription.setText("Seconds");
    }

    @OnClick(R.id.weightIncrease)
    @SuppressWarnings("unused")
    public void onClickIncreaseWeight(View view) {
        if(mSet.isTimed()) {
            mSet.setSeconds(mSet.getSeconds() + 60);

            mActionViewWeightValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewRepsValue.setText(formatSeconds(mSet.getSeconds()));
        } else {
            mSet.setWeight(mSet.getWeight() + 0.5);

            mActionViewWeightValue.setText(String.valueOf(mSet.getWeight()));
        }
    }

    @OnClick(R.id.weightDecrease)
    @SuppressWarnings("unused")
    public void onClickDecreaseWeight(View view) {
        if(mSet.isTimed()) {
            if(mSet.getSeconds() > 60) {
                mSet.setSeconds(mSet.getSeconds() - 60);
            }

            mActionViewWeightValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewRepsValue.setText(formatSeconds(mSet.getSeconds()));
        } else {
            mSet.setWeight(mSet.getWeight() - 0.5);

            mActionViewWeightValue.setText(String.valueOf(mSet.getWeight()));
        }
    }

    @OnClick(R.id.repsIncrease)
    @SuppressWarnings("unused")
    public void onClickIncreaseReps(View view) {
        if(mSet.isTimed()) {
            if(mSet.getSeconds() % 60 == 59) {
                mSet.setSeconds(mSet.getSeconds() - 59);
            } else {
                mSet.setSeconds(mSet.getSeconds() + 1);
            }

            mActionViewWeightValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewRepsValue.setText(formatSeconds(mSet.getSeconds()));
        } else {
            mSet.setReps(mSet.getReps() + 1);

            mActionViewRepsValue.setText(String.valueOf(mSet.getReps()));
        }
    }

    @OnClick(R.id.repsDecrease)
    @SuppressWarnings("unused")
    public void onClickDecreaseReps(View view) {
        if(mSet.isTimed()) {
            if(mSet.getSeconds() % 60 == 0) {
                mSet.setSeconds(mSet.getSeconds() + 59);
            } else {
                mSet.setSeconds(mSet.getSeconds() - 1);
            }

            mActionViewWeightValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewRepsValue.setText(formatSeconds(mSet.getSeconds()));
        } else {
            mSet.setReps(mSet.getReps() - 1);

            mActionViewRepsValue.setText(String.valueOf(mSet.getReps()));
        }
    }

    public String formatReps(int reps) {
        return reps + " x";
    }

    public String formatWeight(double weight) {
        return weight + " kg";
    }

    public String formatMinutes(int seconds) {
        int minutes = seconds / 60;

        if(minutes == 0) {
            return "0";
        }

        return String.valueOf(minutes);
    }

    public String formatSeconds(int seconds) {
        int s = seconds % 60;

        if(s == 0) {
            return "0";
        }

        return String.valueOf(s);
    }
}
