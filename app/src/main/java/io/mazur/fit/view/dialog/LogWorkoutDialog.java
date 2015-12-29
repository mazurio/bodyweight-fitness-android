package io.mazur.fit.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.SectionMode;
import io.mazur.fit.model.WeightMeasurementUnit;
import io.mazur.fit.model.repository.RepositoryExercise;
import io.mazur.fit.model.repository.RepositoryRoutine;
import io.mazur.fit.model.repository.RepositorySet;
import io.mazur.fit.stream.RepositoryStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.PreferenceUtils;
import io.realm.Realm;

public class LogWorkoutDialog {
    public interface OnDismissLogWorkoutDialogListener {
        void onDismissed();
    }

    private static final int MAXIMUM_NUMBER_OF_SETS = 12;

    private Dialog mDialog;
    private OnDismissLogWorkoutDialogListener mOnDismissLogWorkoutDialogListener;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.saveButton)
    Button mSaveButton;

    @InjectView(R.id.setView)
    LinearLayout mSetView;

    @InjectView(R.id.actionView)
    View mActionView;

    @InjectView(R.id.setValue)
    TextView mActionViewSetValue;

    @InjectView(R.id.weightValue)
    TextView mActionViewWeightValue;

    @InjectView(R.id.weightDescription)
    TextView mActionViewWeightDescription;

    @InjectView(R.id.repsValue)
    TextView mActionViewRepsValue;

    @InjectView(R.id.repsDescription)
    TextView mActionViewRepsDescription;

    private LinearLayout mRowLayout;

    private ArrayList<View> mViewSets = new ArrayList<>();

    private RepositoryRoutine mRepositoryRoutine;
    private RepositoryExercise mRepositoryExercise;
    private RepositorySet mSet;

    private Realm mRealm;

    private WeightMeasurementUnit mWeightMeasurementUnit;

    public LogWorkoutDialog(Context context) {
        mRealm = RepositoryStream.getInstance().getRealm();

        Exercise exercise = RoutineStream.getInstance().getExercise();

        mRepositoryRoutine = RepositoryStream.getInstance().getRepositoryRoutineForToday();

        for(RepositoryExercise repositoryExercise : mRepositoryRoutine.getExercises()) {
            if(repositoryExercise.getTitle().equals(exercise.getTitle())) {
                mRepositoryExercise = repositoryExercise;

                break;
            }
        }

        buildDialog(context);
    }

    public LogWorkoutDialog(Context context, RepositoryExercise repositoryExercise) {
        mRealm = RepositoryStream.getInstance().getRealm();

        mRepositoryRoutine = null;
        mRepositoryExercise = repositoryExercise;

        buildDialog(context);
    }

    private void buildDialog(Context context) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_log_workout);
        mDialog.setCanceledOnTouchOutside(true);

        ButterKnife.inject(this, mDialog);

        mWeightMeasurementUnit = PreferenceUtils
                .getInstance()
                .getWeightMeasurementUnit();

        mActionView.setVisibility(View.GONE);
    }

    public void show() {
        mRealm.beginTransaction();

        mToolbar.setTitle(mRepositoryExercise.getTitle());
        mToolbar.setSubtitle(mRepositoryExercise.getDescription());

        inflateToolbarMenu();

        buildSets();

        mSaveButton.setOnClickListener(v -> mDialog.dismiss());
        mDialog.setOnDismissListener(l -> {
            String mode = mRepositoryExercise.getSection().getMode();

            if (mode.equals(SectionMode.LEVELS.toString()) ||
                    mode.equals(SectionMode.PICK.toString())) {

                if (isCompleted(mRepositoryExercise)) {
                    mRepositoryExercise.setVisible(true);
                } else {
                    mRepositoryExercise.setVisible(false);
                }
            }

            mRealm.copyToRealmOrUpdate(mRepositoryExercise);
            mRealm.commitTransaction();

            if (mOnDismissLogWorkoutDialogListener != null) {
                mOnDismissLogWorkoutDialogListener.onDismissed();
            }
        });

        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setOnDismissLogWorkoutDialogListener(OnDismissLogWorkoutDialogListener onDismissLogWorkoutDialogListener) {
        mOnDismissLogWorkoutDialogListener = onDismissLogWorkoutDialogListener;
    }

    public void buildSets() {
        for (RepositorySet set : mRepositoryExercise.getSets()) {
            if (shouldAddSet()) {
                if (set.isTimed()) {
                    addTimedSet(set);
                } else {
                    addSet(set);
                }
            }
        }

        updateToolbarMenu();
    }

    public void actionViewOpened() {
        invalidateToolbarMenu();

        mSaveButton.setText("Back");
        mSaveButton.setOnClickListener(v -> {
            updateSets();

            mSetView.setVisibility(View.VISIBLE);
            mActionView.setVisibility(View.GONE);

            actionViewClosed();
        });
    }

    public void actionViewClosed() {
        inflateToolbarMenu();

        mSaveButton.setText("Save");
        mSaveButton.setOnClickListener(v -> {
            mDialog.dismiss();
        });
    }

    public void updateSets() {
        int i = 0;
        for(View view : mViewSets) {
            RepositorySet set = mRepositoryExercise.getSets().get(i);

            if(set.isTimed()) {
                updateTimedSet(set, view);
            } else {
                updateSet(set, view);
            }

            i++;
        }
    }

    public boolean shouldAddSet() {
        int numberOfSets = mViewSets.size();

        if(numberOfSets >= MAXIMUM_NUMBER_OF_SETS) {
            return false;
        }

        return true;
    }

    public boolean shouldRemoveSet() {
        int numberOfSets = mViewSets.size();

        if(numberOfSets == 1) {
            return false;
        }

        return true;
    }

    public void addRow() {
        int numberOfSets = mViewSets.size();

        if(numberOfSets == 0 || numberOfSets == 3 || numberOfSets == 6 || numberOfSets == 9) {
            View view = LayoutInflater.from(mDialog.getContext())
                    .inflate(R.layout.view_dialog_log_workout_row, mSetView, false);

            mRowLayout = (LinearLayout) view;
            mSetView.addView(view);
        }
    }

    public boolean addSet(RepositorySet set) {
        addRow();

        View view = LayoutInflater.from(mDialog.getContext())
                .inflate(R.layout.view_dialog_log_workout_set, mSetView, false);

        updateSet(set, view);

        view.setOnClickListener(v -> {
            updateActionView(set, mRepositoryExercise.getSets().indexOf(set) + 1);

            mSetView.setVisibility(View.GONE);
            mActionView.setVisibility(View.VISIBLE);

            actionViewOpened();
        });

        mRowLayout.addView(view);

        if(!mRepositoryExercise.getSets().contains(set)) {
            mRepositoryExercise.getSets().add(set);
        }

        mViewSets.add(view);

        setLastUpdatedTime();

        return true;
    }

    public boolean addTimedSet(RepositorySet set) {
        addRow();

        View view = LayoutInflater.from(mDialog.getContext())
                .inflate(R.layout.view_dialog_log_workout_timed_set, mSetView, false);

        updateTimedSet(set, view);

        view.setOnClickListener(v -> {
            updateActionViewForTimedSet(set, mRepositoryExercise.getSets().indexOf(set) + 1);

            mSetView.setVisibility(View.GONE);
            mActionView.setVisibility(View.VISIBLE);

            actionViewOpened();
        });

        mRowLayout.addView(view);

        if(!mRepositoryExercise.getSets().contains(set)) {
            mRepositoryExercise.getSets().add(set);
        }

        setLastUpdatedTime();

        mViewSets.add(view);

        return true;
    }

    public boolean removeLastSet() {
        mRepositoryExercise.getSets().remove(mRepositoryExercise.getSets().size() - 1);
        mViewSets.remove(mViewSets.size() - 1);

        mRowLayout.removeViewAt(mRowLayout.getChildCount() - 1);

        if (mRowLayout.getChildCount() == 0) {
            mSetView.removeView(mRowLayout);

            mRowLayout = (LinearLayout) mSetView.getChildAt(mSetView.getChildCount() - 1);
        }

        return true;
    }

    public void updateActionView(RepositorySet set, int index) {
        mSet = set;

        mActionViewSetValue.setText(String.valueOf(index));

        mActionViewWeightValue.setText(String.valueOf(set.getWeight()));
        mActionViewWeightDescription.setText(formatWeightDescription());

        mActionViewRepsValue.setText(String.valueOf(set.getReps()));
        mActionViewRepsDescription.setText("Reps");
    }

    public void updateActionViewForTimedSet(RepositorySet set, int index) {
        mSet = set;

        mActionViewSetValue.setText(String.valueOf(index));

        mActionViewRepsValue.setText(formatMinutes(mSet.getSeconds()));
        mActionViewRepsDescription.setText("Minutes");

        mActionViewWeightValue.setText(formatSeconds(mSet.getSeconds()));
        mActionViewWeightDescription.setText("Seconds");
    }

    public void updateSet(RepositorySet set, View view) {
        TextView repsOnlyValue = (TextView) view.findViewById(R.id.repsOnlyValue);

        TextView reps = (TextView) view.findViewById(R.id.repsValue);
        TextView weight = (TextView) view.findViewById(R.id.weightValue);

        View center = view.findViewById(R.id.center);

        if(set.getWeight() == 0) {
            repsOnlyValue.setVisibility(View.VISIBLE);

            reps.setVisibility(View.GONE);
            weight.setVisibility(View.GONE);

            center.setVisibility(View.GONE);

            repsOnlyValue.setText(formatReps(set.getReps(), false));
        } else {
            repsOnlyValue.setVisibility(View.GONE);

            reps.setVisibility(View.VISIBLE);
            weight.setVisibility(View.VISIBLE);

            center.setVisibility(View.VISIBLE);

            reps.setText(formatReps(set.getReps(), true));
            weight.setText(formatWeight(set.getWeight()));
        }
    }

    public void updateTimedSet(RepositorySet set, View view) {
        TextView secondsOnlyValue = (TextView) view.findViewById(R.id.secondsOnlyValue);

        TextView minutes = (TextView) view.findViewById(R.id.minutesValue);
        TextView seconds = (TextView) view.findViewById(R.id.secondsValue);

        View center = view.findViewById(R.id.center);

        if(set.getSeconds() < 60) {
            secondsOnlyValue.setVisibility(View.VISIBLE);

            minutes.setVisibility(View.GONE);
            seconds.setVisibility(View.GONE);

            center.setVisibility(View.GONE);

            secondsOnlyValue.setText(formatSeconds(
                    set.getSeconds()
            ) + "s");
        } else {
            secondsOnlyValue.setVisibility(View.GONE);

            minutes.setVisibility(View.VISIBLE);
            seconds.setVisibility(View.VISIBLE);

            center.setVisibility(View.VISIBLE);

            minutes.setText(formatMinutes(set.getSeconds()) + "m");
            seconds.setText(formatSeconds(set.getSeconds()) + "s");
        }
    }

    public void updateToolbarMenu() {
        int numberOfSets = mViewSets.size();

        Menu menu = mToolbar.getMenu();

        if(numberOfSets >= MAXIMUM_NUMBER_OF_SETS) {
            menu.findItem(R.id.action_add_set).setVisible(false);
            menu.findItem(R.id.action_add_timed_set).setVisible(false);
        } else if (numberOfSets == 1) {
            if (mRepositoryExercise.getDefaultSet().equals("timed")) {
                menu.findItem(R.id.action_add_set).setVisible(false);
                menu.findItem(R.id.action_add_timed_set).setVisible(true);
            } else {
                menu.findItem(R.id.action_add_set).setVisible(true);
                menu.findItem(R.id.action_add_timed_set).setVisible(false);
            }

            menu.findItem(R.id.action_remove_last_set).setVisible(false);
        } else {
            if (mRepositoryExercise.getDefaultSet().equals("timed")) {
                menu.findItem(R.id.action_add_set).setVisible(false);
                menu.findItem(R.id.action_add_timed_set).setVisible(true);
            } else {
                menu.findItem(R.id.action_add_set).setVisible(true);
                menu.findItem(R.id.action_add_timed_set).setVisible(false);
            }

            menu.findItem(R.id.action_remove_last_set).setVisible(true);
        }
    }

    public void inflateToolbarMenu() {
        mToolbar.inflateMenu(R.menu.menu_log_workout);
        mToolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_set: {
                    if (shouldAddSet()) {
                        RepositorySet repositorySet = mRealm.createObject(RepositorySet.class);

                        repositorySet.setId("Set-" + UUID.randomUUID().toString());
                        repositorySet.setIsTimed(false);
                        repositorySet.setSeconds(0);
                        repositorySet.setWeight(0);
                        repositorySet.setReps(0);

                        addSet(repositorySet);
                        updateToolbarMenu();
                    }

                    return true;
                }

                case R.id.action_add_timed_set: {
                    if (shouldAddSet()) {
                        RepositorySet repositorySet = mRealm.createObject(RepositorySet.class);

                        repositorySet.setId("Set-" + UUID.randomUUID().toString());
                        repositorySet.setIsTimed(true);
                        repositorySet.setSeconds(0);
                        repositorySet.setWeight(0);
                        repositorySet.setReps(0);

                        addTimedSet(repositorySet);
                        updateToolbarMenu();
                    }

                    return true;
                }

                case R.id.action_remove_last_set: {
                    if (shouldRemoveSet()) {
                        removeLastSet();
                        updateToolbarMenu();
                    }

                    return true;
                }
            }

            return false;
        });

        updateToolbarMenu();
    }

    public void invalidateToolbarMenu() {
        mToolbar.getMenu().clear();
    }

    @OnClick(R.id.weightIncrease)
    @SuppressWarnings("unused")
    public void onClickIncreaseWeight(View view) {
        if(mSet.isTimed()) {
            if(mSet.getSeconds() % 60 == 59) {
                mSet.setSeconds(mSet.getSeconds() - 59);
            } else {
                mSet.setSeconds(mSet.getSeconds() + 1);
            }

            mActionViewRepsValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewWeightValue.setText(formatSeconds(mSet.getSeconds()));

            setLastUpdatedTime();
        } else {
            if(mSet.getWeight() >= 500) {
                return;
            }

            if (mWeightMeasurementUnit.equals(WeightMeasurementUnit.kg)) {
                mSet.setWeight(mSet.getWeight() + 0.5);
            } else {
                mSet.setWeight(mSet.getWeight() + 1.0);
            }

            mActionViewWeightValue.setText(String.valueOf(mSet.getWeight()));

            setLastUpdatedTime();
        }
    }

    @OnClick(R.id.weightDecrease)
    @SuppressWarnings("unused")
    public void onClickDecreaseWeight(View view) {
        if(mSet.isTimed()) {
            if(mSet.getSeconds() % 60 == 0) {
                mSet.setSeconds(mSet.getSeconds() + 59);
            } else {
                mSet.setSeconds(mSet.getSeconds() - 1);
            }

            mActionViewRepsValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewWeightValue.setText(formatSeconds(mSet.getSeconds()));

            setLastUpdatedTime();
        } else {
            if(mSet.getWeight() <= 0) {
                return;
            }

            if (mWeightMeasurementUnit.equals(WeightMeasurementUnit.kg)) {
                mSet.setWeight(mSet.getWeight() - 0.5);
            } else {
                mSet.setWeight(mSet.getWeight() - 1.0);
            }

            mActionViewWeightValue.setText(String.valueOf(mSet.getWeight()));

            setLastUpdatedTime();
        }
    }

    @OnClick(R.id.repsIncrease)
    @SuppressWarnings("unused")
    public void onClickIncreaseReps(View view) {
        if(mSet.isTimed()) {
            if (mSet.getSeconds() / 60 >= 5) {
                return;
            }

            mSet.setSeconds(mSet.getSeconds() + 60);

            mActionViewRepsValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewWeightValue.setText(formatSeconds(mSet.getSeconds()));

            setLastUpdatedTime();
        } else {
            if(mSet.getReps() >= 50) {
                return;
            }

            mSet.setReps(mSet.getReps() + 1);

            mActionViewRepsValue.setText(String.valueOf(mSet.getReps()));

            setLastUpdatedTime();
        }
    }

    @OnClick(R.id.repsDecrease)
    @SuppressWarnings("unused")
    public void onClickDecreaseReps(View view) {
        if(mSet.isTimed()) {
            if(mSet.getSeconds() >= 60) {
                mSet.setSeconds(mSet.getSeconds() - 60);
            }

            mActionViewRepsValue.setText(formatMinutes(mSet.getSeconds()));
            mActionViewWeightValue.setText(formatSeconds(mSet.getSeconds()));

            setLastUpdatedTime();
        } else {
            if(mSet.getReps() == 0) {
                return;
            }

            mSet.setReps(mSet.getReps() - 1);

            mActionViewRepsValue.setText(String.valueOf(mSet.getReps()));

            setLastUpdatedTime();
        }
    }

    public String formatReps(int reps, boolean append) {
        if(append) {
            return reps + " x";
        } else {
            if(reps == 0) {
                return "/";
            }

            return String.valueOf(reps);
        }
    }

    public String formatWeight(double weight) {
        return String.format("%s %s", weight, mWeightMeasurementUnit.toString()
        );
    }

    public String formatWeightDescription() {
        return String.format("Weight (%s)", mWeightMeasurementUnit.toString());
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

    public boolean isCompleted(RepositoryExercise repositoryExercise) {
        int size = repositoryExercise.getSets().size();

        if (size == 0) {
            return false;
        }

        RepositorySet firstSet = repositoryExercise.getSets().get(0);

        if(size == 1 && firstSet.getSeconds() == 0 && firstSet.getReps() == 0) {
            return false;
        }

        return true;
    }

    public void setLastUpdatedTime() {
        /**
         * Update last updated time.
         *
         * Update only if the difference between start time and last updated time is less than
         * 120 minutes (it ignores changing the time later in the day after the workout ends
         * if we decide to update some values).
         */
        if (mRepositoryRoutine != null) {
            DateTime startTime = new DateTime(mRepositoryRoutine.getStartTime());
            DateTime lastUpdatedTime = new DateTime(mRepositoryRoutine.getLastUpdatedTime());

            Duration duration = new Duration(startTime, lastUpdatedTime);

            if (duration.toStandardMinutes().getMinutes() < 120) {
                mRepositoryRoutine.setLastUpdatedTime(new DateTime().toDate());
            }
        }
    }
}
