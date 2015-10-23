package io.mazur.fit.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.fragment.NavigationDrawerFragment;
import io.mazur.fit.model.ActivityState;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.stream.ActivityStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.PreferenceUtil;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Exercise mExercise;

    @InjectView(R.id.view_home) View mViewHome;
    @InjectView(R.id.view_calendar) View mViewCalendar;
    @InjectView(R.id.view_calendar_pager) ViewPager mViewCalendarPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

		setToolbar();
        setDrawer();

        RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
            mExercise = exercise;
            invalidateOptionsMenu();
        });

        keepScreenOnWhenAppIsRunning();

        mViewCalendarPager.setAdapter(new CalendarAdapter());
	}

    /**
     * TODO: Kept here for reference as we need to change the color of Calendar Status Bar.
     */
    public void changeStatusBarColor(String colorString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(colorString));
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();

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

		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        keepScreenOnWhenAppIsRunning();
    }

    private void setToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
                actionBar.setTitle(exercise.getTitle());
                actionBar.setSubtitle(exercise.getDescription());
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
                    mViewHome.setVisibility(View.VISIBLE);
                    mViewCalendar.setVisibility(View.GONE);

                    ActionBar actionBar = getSupportActionBar();
                    if(actionBar != null) {
                        changeStatusBarColor("#00453E"); // primaryDarkGreen

                        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));
                        actionBar.setTitle("Home");
                        actionBar.setSubtitle("");
                    }

                    break;
                }

                case R.id.action_menu_workout_log: {
                    mViewHome.setVisibility(View.GONE);
                    mViewCalendar.setVisibility(View.VISIBLE);

                    ActionBar actionBar = getSupportActionBar();
                    if(actionBar != null) {
                        changeStatusBarColor("#25242F"); // primaryDarkGrey

                        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E2E3B")));
                        actionBar.setTitle("Calendar");
                        actionBar.setSubtitle("");
                    }

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
