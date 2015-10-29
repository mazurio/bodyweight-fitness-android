package io.mazur.fit.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.model.realm.RealmSet;
import io.mazur.fit.stream.RealmStream;

import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.Logger;
import io.realm.Realm;

public class LogWorkoutDialog {
    private Context mContext;
    private Dialog mDialog;

    private int mNumberOfSets = 0;

    private ArrayList<LinearLayout> mSetLayouts = new ArrayList<>();

    private boolean mEditTextHasFocus = false;

    private Exercise mCurrentExercise;

    private Realm mRealm;
    private DateTime mRealmDateTime;
    private RealmRoutine mRealmRoutine;
    private RealmExercise mRealmExercise;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.layout) LinearLayout mMainLayout;
    @InjectView(R.id.saveButton) Button mSaveButton;

    public LogWorkoutDialog(Context context) {
        mContext = context;
        mRealm = RealmStream.getInstance().getRealm();

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_log_workout);
        mDialog.setCanceledOnTouchOutside(true);

        mRealmRoutine = RealmStream
                .getInstance()
                .getRealmRoutineForToday();

        mRealmDateTime = new DateTime(mRealmRoutine.getDate());

        mCurrentExercise = RoutineStream.getInstance().getExercise();

        /**
         * TODO: This should be based on the unique id/key of the exercise.
         */
        for(RealmExercise realmExercise : mRealmRoutine.getExercises()) {
            if(realmExercise.getTitle().equals(mCurrentExercise.getTitle())) {
                mRealmExercise = realmExercise;

                break;
            }
        }
    }

    public void show() {
        mRealm.beginTransaction();

        ButterKnife.inject(this, mDialog);

        mToolbar.inflateMenu(R.menu.menu_log_workout);

        if(!mCurrentExercise.allowBodyweightReps()) {
            mToolbar.getMenu().removeItem(R.id.action_add_bodyweight_set);
        }

        if(!mCurrentExercise.allowTimeReps()) {
            mToolbar.getMenu().removeItem(R.id.action_add_time_set);
        }

        mToolbar.setTitle(mRealmExercise.getTitle());
        mToolbar.setSubtitle(mRealmDateTime.toString("EEEE, d MMMM"));
        mToolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_bodyweight_set:
                    addSet(null, RealmSet.BODYWEIGHT);

                    return true;

                case R.id.action_add_time_set:
                    addSet(null, RealmSet.TIME);

                    return true;

                case R.id.action_remove_set:
                    removeSet();

                    return true;
            }

            return false;
        });

        for(RealmSet set : mRealmExercise.getSets()) {
            addSet(set, set.getType());
        }

        mDialog.setOnDismissListener(dialog -> {
            mRealm.copyToRealmOrUpdate(mRealmRoutine);
            mRealm.commitTransaction();
        });

        mSaveButton.setOnClickListener(v -> {
            mDialog.dismiss();
        });

        mDialog.show();
    }

    public void addSet(RealmSet realmSet, String setType) {
        if(mNumberOfSets >= 12) {
            return;
        }

        LinearLayout setLayout = null;

        boolean isTimeSet = setType.equals(RealmSet.TIME);

        int numberOfSetsInRow = 4;
        if(isTimeSet) {
            numberOfSetsInRow = 1;
        }

        if(realmSet == null) {
            realmSet = mRealm.createObject(RealmSet.class);
            realmSet.setId(UUID.randomUUID().toString());
            realmSet.setType(setType);
            realmSet.setValue(0);

            mRealmExercise.getSets().add(realmSet);
        }

        if(mNumberOfSets == 0 || (mNumberOfSets % numberOfSetsInRow) == 0) {
            setLayout = (LinearLayout) LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.view_dialog_log_workout_row, mMainLayout, false);

            mMainLayout.addView(setLayout);
            mSetLayouts.add(setLayout);
        } else {
            setLayout = mSetLayouts.get(mSetLayouts.size() - 1);
        }

        if(setLayout != null) {
            CardView setView;

            if(isTimeSet) {
                View view = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.view_dialog_log_workout_time_set, setLayout, false);

                EditText editText = (EditText) view.findViewById(R.id.editText);

                if(realmSet.getValue() == 0) {
                    editText.setText("/");
                } else {
                    editText.setText(prettyFormatSeconds(realmSet.getValue()));
                }

                final RealmSet set = realmSet;

                editText.setOnFocusChangeListener((v, hasFocus) -> {
                    mEditTextHasFocus = hasFocus;

                    if(hasFocus) {
                        editText.setText(String.valueOf(set.getValue()));
                    } else {
                        editText.setText(prettyFormatSeconds(set.getValue()));
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (mEditTextHasFocus) {
                            String s = charSequence.toString();

                            if(s.equals("")) {
                                set.setValue(0);
                            } else {
                                int value;

                                try {
                                    value = Integer.valueOf(s);
                                } catch (NumberFormatException e) {
                                    value = 0;
                                }

                                if(value > 600) {
                                    value = 600;
                                } else if(value < 0) {
                                    value = 0;
                                }

                                set.setValue(value);
                            }
                        }
                    }

                    @Override public void afterTextChanged(Editable editable) {}
                });

                setLayout.addView(view);
            } else {
                setView = (CardView) LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.view_dialog_log_workout_set, setLayout, false);

                Button button = (Button) setView.findViewById(R.id.button);

                if(realmSet.getValue() == 0) {
                    button.setText("/");
                } else {
                    button.setText(String.valueOf(realmSet.getValue()));
                }

                final RealmSet set = realmSet;
                button.setOnClickListener(v -> {
                    if(set.getValue() >= 10) {
                        set.setValue(0);

                        button.setText("/");
                    } else {
                        set.setValue(set.getValue() + 1);

                        button.setText(String.valueOf(set.getValue()));
                    }
                });

                setLayout.addView(setView);
            }

            mNumberOfSets += 1;
        }
    }

    public void removeSet() {
        if(mNumberOfSets == 1) {
            return;
        }

        mRealmExercise.getSets().remove(mRealmExercise.getSets().size() - 1);

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

    public String prettyFormatSeconds(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;

        if(seconds > 60) {
            if(s == 0) {
                return m + "m";
            }

            return m + "m" + s + "s";
        }

        return seconds + "s";
    }
}
