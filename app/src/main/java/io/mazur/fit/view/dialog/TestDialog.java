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

public class TestDialog {
    private Context mContext;
    private Dialog mDialog;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.saveButton) Button mSaveButton;

    public TestDialog(Context context) {
        mContext = context;

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_test);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        ButterKnife.inject(this, mDialog);

        mToolbar.setTitle("Title");
        mToolbar.setSubtitle("Subtitle");
        mToolbar.inflateMenu(R.menu.menu_log_workout);
        mToolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_add_bodyweight_set:
                    return true;

                case R.id.action_add_time_set:
                    return true;

                case R.id.action_remove_set:
                    return true;
            }

            return false;
        });

        mSaveButton.setOnClickListener(v -> {
            mDialog.dismiss();
        });

        mDialog.show();
    }
}
