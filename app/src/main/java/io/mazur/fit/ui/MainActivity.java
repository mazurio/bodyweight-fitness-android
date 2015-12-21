package io.mazur.fit.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.model.ActivityPresenterState;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.utils.DateUtils;
import io.mazur.fit.view.CalendarView;
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

    @InjectView(R.id.action_view_log_workout_button) Fab mLogWorkoutButton;
    @InjectView(R.id.action_view_action_button) Fab mActionButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

		setToolbar();
        setDrawer();

        keepScreenOnWhenAppIsRunning();

        mViewCalendar.getCalendarPresenter()
                .getCalendarAdapter()
                .getOnDaySelectedObservable()
                .subscribe(calendarDayChanged -> {
                    DateTime dateTime = DateUtils.getDate(
                            calendarDayChanged.presenterSelected,
                            calendarDayChanged.daySelected
                    );

                    mToolbarSectionTitle.setText(dateTime.toString("YYYY", Locale.ENGLISH));
                    mToolbarExerciseTitle.setText(dateTime.toString("MMMM", Locale.ENGLISH));

                    mToolbarExerciseDescription.setText("");
                });
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

        ActivityStream.getInstance().setMenuState(item.getItemId());

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
                    ActivityStream.getInstance()
                            .setActivityPresenterState(ActivityPresenterState.Home);

                    mDisplayMenu = false;

                    invalidateOptionsMenu();

                    mViewHome.setVisibility(View.VISIBLE);
                    mViewCalendar.setVisibility(View.GONE);

                    mLogWorkoutButton.setVisibility(View.VISIBLE);
                    mActionButton.setVisibility(View.VISIBLE);

                    ActionBar actionBar = getSupportActionBar();
                    if(actionBar != null) {
                        Exercise exercise = RoutineStream.getInstance().getExercise();

                        mToolbarExerciseTitle.setText(exercise.getTitle());
                        mToolbarSectionTitle.setText(exercise.getSection().getTitle());
                        mToolbarExerciseDescription.setText(exercise.getDescription());
                    }

                    break;
                }

                case R.id.action_menu_workout_log: {
                    ActivityStream.getInstance()
                            .setActivityPresenterState(ActivityPresenterState.Calendar);

                    mDisplayMenu = true;

                    invalidateOptionsMenu();

                    mViewHome.setVisibility(View.GONE);
                    mViewCalendar.setVisibility(View.VISIBLE);

                    mLogWorkoutButton.setVisibility(View.GONE);
                    mActionButton.setVisibility(View.GONE);

                    CalendarDayChanged calendarDayChanged = mViewCalendar.getCalendarPresenter().getCalendarDayChanged();

                    DateTime dateTime;
                    if(calendarDayChanged == null) {
                        dateTime = new DateTime();
                    } else {
                        dateTime = DateUtils.getDate(
                                calendarDayChanged.presenterSelected,
                                calendarDayChanged.daySelected
                        );
                    }

                    mToolbarSectionTitle.setText(dateTime.toString("YYYY", Locale.ENGLISH));
                    mToolbarExerciseTitle.setText(dateTime.toString("MMMM", Locale.ENGLISH));

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
