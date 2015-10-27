package io.mazur.fit.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.realm.RealmExercise;
import io.mazur.fit.realm.RealmRoutine;
import io.mazur.fit.realm.RealmSet;
import io.mazur.fit.stream.RealmStream;

import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.Logger;
import io.realm.Realm;

public class LogWorkoutDialog {
    private Context mContext;
    private Dialog mDialog;

    private int mNumberOfSets = 0;

    private ArrayList<LinearLayout> mSetLayouts = new ArrayList<>();

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

        Exercise currentExercise = RoutineStream.getInstance().getExercise();

        /**
         * TODO: This should be based on the unique id of the exercise.
         */
        for(RealmExercise realmExercise : mRealmRoutine.getExercises()) {
            if(realmExercise.getTitle().equals(currentExercise.getTitle())) {
                mRealmExercise = realmExercise;

                break;
            }
        }
    }

    public void show() {
        mRealm.beginTransaction();

        ButterKnife.inject(this, mDialog);

        mToolbar.inflateMenu(R.menu.menu_log_workout);
        mToolbar.setTitle(mRealmExercise.getTitle());
        mToolbar.setSubtitle(mRealmDateTime.toString("EEEE, d MMMM"));
        mToolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_set:
                    addSet(null);

                    return true;

                case R.id.action_remove_set:
                    removeSet();

                    return true;
            }

            return false;
        });

        for(RealmSet set : mRealmExercise.getSets()) {
            addSet(set);
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

    public void addSet(RealmSet realmSet) {
        if(mNumberOfSets >= 12) {
            return;
        }

        LinearLayout setLayout = null;

        if(realmSet == null) {
            realmSet = mRealm.createObject(RealmSet.class);
            realmSet.setId(UUID.randomUUID().toString());
            realmSet.setNumberOfReps(0);

            mRealmExercise.getSets().add(realmSet);
        }

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

            Button button = (Button) setView.findViewById(R.id.button);

            if(realmSet.getNumberOfReps() == 0) {
                button.setText("/");
            } else {
                button.setText(String.valueOf(realmSet.getNumberOfReps()));
            }

            final RealmSet set = realmSet;
            button.setOnClickListener(v -> {
                if(set.getNumberOfReps() >= 10) {
                    set.setNumberOfReps(0);

                    button.setText("/");
                } else {
                    set.setNumberOfReps(set.getNumberOfReps() + 1);

                    button.setText(String.valueOf(set.getNumberOfReps()));
                }
            });

            setLayout.addView(setView);

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
}
