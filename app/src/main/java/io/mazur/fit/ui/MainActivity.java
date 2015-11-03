package io.mazur.fit.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.view.CalendarView;
import io.mazur.fit.view.dialog.LogWorkoutDialog;
import io.mazur.fit.view.dialog.ProgressDialog;
import io.mazur.fit.view.fragment.NavigationDrawerFragment;
import io.mazur.fit.model.ActivityState;
import io.mazur.fit.stream.ActivityStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.PreferenceUtil;
import io.mazur.fit.view.widget.Fab;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private boolean mDisplayMenu = false;

    @InjectView(R.id.view_home) View mViewHome;
    @InjectView(R.id.view_calendar) CalendarView mViewCalendar;

    @InjectView(R.id.toolbar_exercise_title) TextView mToolbarExerciseTitle;
    @InjectView(R.id.toolbar_section_title) TextView mToolbarSectionTitle;
    @InjectView(R.id.toolbar_exercise_description) TextView mToolbarExerciseDescription;

    /**
     * TODO: Move to ActionPresenter/ActionView.
     */
    @InjectView(R.id.log_workout_button) Fab mLogWorkoutButton;
    @InjectView(R.id.action_button) Fab mActionButton;
    @InjectView(R.id.fab_sheet) CardView mFabSheet;
    @InjectView(R.id.action_choose_progression) TextView mActionChooseProgression;
    @InjectView(R.id.action_watch_on_youtube) TextView mActionWatchOnYouTube;
    @InjectView(R.id.overlay) View mOverlay;

    private MaterialSheetFab mMaterialSheetFab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

		setToolbar();
        setDrawer();

        keepScreenOnWhenAppIsRunning();

        RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
            if(exercise.hasProgressions()) {
                mActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_assessment_white_24dp));
                mActionChooseProgression.setVisibility(View.VISIBLE);
            } else {
                mActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                mActionChooseProgression.setVisibility(View.GONE);
            }
        });

        mLogWorkoutButton.setOnClickListener(v -> {
            LogWorkoutDialog logWorkoutDialog = new LogWorkoutDialog(this);
            logWorkoutDialog.show();
        });

        mActionChooseProgression.setOnClickListener(v -> {
            mMaterialSheetFab.hideSheet();

            ProgressDialog progressDialog = new ProgressDialog(this, RoutineStream.getInstance().getExercise());
            progressDialog.show();
        });

        mActionWatchOnYouTube.setOnClickListener(v -> {
            mMaterialSheetFab.hideSheet();

            String id = RoutineStream.getInstance().getExercise().getYouTubeId();

            if(id != null) {
                try {
                    startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id))
                    );
                } catch(ActivityNotFoundException e) {
                    startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id))
                    );
                }
            }
        });

        mMaterialSheetFab = new MaterialSheetFab<>(mActionButton, mFabSheet, mOverlay, Color.parseColor("#FFFFFF"), Color.parseColor("#FFFDDD"));
        mMaterialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();

                mLogWorkoutButton.hide();
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

                mLogWorkoutButton.show();
            }
        });

//        startActivity(new Intent(this, ProgressActivity.class));
	}

    @Override
    protected void onPause() {
        super.onPause();

        ActivityStream.getInstance().setActivityState(ActivityState.OnPause);
    }

    @Override
    protected void onStop() {
        super.onStop();

        clearFlagKeepScreenOn();

        RealmStream.getInstance().getRealm().close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityStream.getInstance().setActivityState(ActivityState.OnResume);

        keepScreenOnWhenAppIsRunning();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mNavigationDrawerFragment.getActionBarDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mNavigationDrawerFragment.getActionBarDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if(mNavigationDrawerFragment.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
            case(R.id.action_today): {
                // MenuStream.onNext(actionToday);

                return true;
            }
        }

		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        keepScreenOnWhenAppIsRunning();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mDisplayMenu) {
            getMenuInflater().inflate(R.menu.calendar, menu);
        }

        return true;
    }

    private void setToolbar() {
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
                mToolbarExerciseTitle.setText(exercise.getTitle());
                mToolbarSectionTitle.setText(exercise.getSection().getTitle());
                mToolbarExerciseDescription.setText(exercise.getDescription());
            });

            actionBar.setElevation(0);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
	}

    private void setDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer_fragment);
        mNavigationDrawerFragment.setDrawer(R.id.drawer_fragment, (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.getMenuObservable().subscribe(id -> {
            switch(id) {
                case R.id.action_menu_home: {
                    mDisplayMenu = false;

                    invalidateOptionsMenu();

                    mViewHome.setVisibility(View.VISIBLE);
                    mViewCalendar.setVisibility(View.GONE);

                    mLogWorkoutButton.setVisibility(View.VISIBLE);
                    mActionButton.setVisibility(View.VISIBLE);

                    ActionBar actionBar = getSupportActionBar();
                    if(actionBar != null) {
                        Exercise exercise = RoutineStream.getInstance().getExercise();

                        actionBar.setTitle(exercise.getTitle());
                        actionBar.setSubtitle(exercise.getDescription());
                    }

                    break;
                }

                case R.id.action_menu_workout_log: {
                    mDisplayMenu = true;

                    invalidateOptionsMenu();

                    mViewHome.setVisibility(View.GONE);
                    mViewCalendar.setVisibility(View.VISIBLE);

                    mLogWorkoutButton.setVisibility(View.GONE);
                    mActionButton.setVisibility(View.GONE);

                    mToolbarExerciseTitle.setText("");
                    mToolbarSectionTitle.setText("");
                    mToolbarExerciseDescription.setText("");

                    break;
                }

                case R.id.action_menu_faq: {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://www.reddit.com/r/bodyweightfitness/wiki/faq")));

                    break;
                }

                case R.id.action_menu_settings: {
                    startActivity(new Intent(
                            getApplicationContext(),
                            SettingsActivity.class));

                    break;
                }

                case R.id.action_menu_help_and_feedback: {

                    break;
                }
            }
        });
    }

    private void keepScreenOnWhenAppIsRunning() {
        if(PreferenceUtil.getInstance().keepScreenOnWhenAppIsRunning()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            clearFlagKeepScreenOn();
        }
    }

    private void clearFlagKeepScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
