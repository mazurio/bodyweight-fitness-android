package com.bodyweight.fitness.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.stream.Dialog;
import com.bodyweight.fitness.stream.DialogType;
import com.bodyweight.fitness.stream.RepositoryStream;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.stream.Stream;
import com.bodyweight.fitness.stream.UiEvent;
import com.bodyweight.fitness.utils.ApplicationStoreUtils;
import com.bodyweight.fitness.utils.PreferenceUtils;

import com.bodyweight.fitness.view.dialog.LogWorkoutDialog;
import com.bodyweight.fitness.view.dialog.ProgressDialog;
import com.kobakei.ratethisapp.RateThisApp;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.functions.Action1;

public class MainActivity extends RxAppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Integer mId = R.id.action_menu_home;

    private TabLayout mTabLayout;

    private View mTimerView;
    private View mRepsLoggerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mId = savedInstanceState.getInt("ID");
        }

		setContentView(R.layout.activity_main);

        setToolbar();
        setTabLayout();
        keepScreenOnWhenAppIsRunning();
    }

    @Override
    protected void onResume() {
        super.onResume();

        keepScreenOnWhenAppIsRunning();
        subscribe();
    }

    @Override
    protected void onStart() {
        super.onStart();

        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        clearFlagKeepScreenOn();

        RepositoryStream.getInstance()
                .getRealm()
                .close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("ID", mId);

        super.onSaveInstanceState(outState);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Stream.INSTANCE.setMenu(item.getItemId());

		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        keepScreenOnWhenAppIsRunning();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (mId.equals(R.id.action_menu_home)) {
            getMenuInflater().inflate(R.menu.home, menu);
        } else if (mId.equals(R.id.action_menu_workout_log)) {
            getMenuInflater().inflate(R.menu.calendar, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener((item) -> {
                drawerLayout.closeDrawers();

                Stream.INSTANCE.setDrawer(item.getItemId());

                if (item.getItemId() == R.id.action_menu_support_developer || item.getItemId() == R.id.action_menu_settings) {
                    return false;
                } else {
                    return true;
                }
            });
        }
	}

    private void setTabLayout() {
        mTimerView = findViewById(R.id.timer_view);
        mRepsLoggerView = findViewById(R.id.reps_logger_view);

        mTabLayout = (TabLayout) findViewById(R.id.view_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText("Timer"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Reps Logger"));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    mTimerView.setVisibility(View.VISIBLE);
                    mRepsLoggerView.setVisibility(View.GONE);
                } else {
                    mTimerView.setVisibility(View.GONE);
                    mRepsLoggerView.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void keepScreenOnWhenAppIsRunning() {
        if (PreferenceUtils.getInstance().keepScreenOnWhenAppIsRunning()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            clearFlagKeepScreenOn();
        }
    }

    private void subscribe() {
        UiEvent.INSTANCE.getDialogObservable()
                .compose(bindToLifecycle())
                .subscribe(new Action1<Dialog>() {
                    @Override
                    public void call(Dialog dialog) {
                        if (dialog.getDialogType() == DialogType.LogWorkout) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.INSTANCE.getExerciseId(), dialog.getExerciseId());

                            LogWorkoutDialog logWorkoutDialog = new LogWorkoutDialog();
                            logWorkoutDialog.setArguments(bundle);

                            logWorkoutDialog.show(getSupportFragmentManager(), "logWorkoutDialog");
                        } else if (dialog.getDialogType() == DialogType.Progress) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.INSTANCE.getExerciseId(), dialog.getExerciseId());

                            ProgressDialog progressDialog = new ProgressDialog();
                            progressDialog.setArguments(bundle);

                            progressDialog.show(getSupportFragmentManager(), "progressDialog");
                        }
                    }
                });

        RoutineStream.getInstance()
                .getExerciseObservable()
                .compose(bindToLifecycle())
                .subscribe(exercise -> {
                    if (exercise.isTimedSet()) {
                        if (mTabLayout.getTabCount() == 2) {
                            mTabLayout.removeAllTabs();
                            mTabLayout.addTab(mTabLayout.newTab().setText("Timer"));
                        }

                        mTabLayout.getTabAt(0).select();
                    } else {
                        if (mTabLayout.getTabCount() == 1) {
                            mTabLayout.removeAllTabs();

                            mTabLayout.addTab(mTabLayout.newTab().setText("Timer"));
                            mTabLayout.addTab(mTabLayout.newTab().setText("Reps Logger"));
                        }

                        mTabLayout.getTabAt(1).select();
                    }
                });

        Stream.INSTANCE
                .getMenuObservable()
                .compose(bindToLifecycle())
                .filter(id -> id.equals(R.id.action_dashboard))
                .subscribe(id -> {
                    startActivity(new Intent(this, DashboardActivity.class));
                });


        Stream.INSTANCE
                .getDrawerObservable()
                .compose(bindToLifecycle())
                .filter(id ->
                        id.equals(R.id.action_menu_home) || id.equals(R.id.action_menu_workout_log))
                .subscribe(id -> {
                    mId = id;
                });

        Stream.INSTANCE
                .getDrawerObservable()
                .compose(bindToLifecycle())
                .subscribe(id -> {
                    switch (id) {
                        case (R.id.action_menu_support_developer): {
                            String installerPackageName = getPackageManager()
                                    .getInstallerPackageName(getPackageName());

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(ApplicationStoreUtils
                                    .getApplicationStoreUrl(installerPackageName)
                                    .getApplicationStoreAppUrl()
                            ));

                            startActivity(intent);

                            break;
                        }

                        case (R.id.action_menu_settings): {
                            startActivity(
                                    new Intent(getApplicationContext(), SettingsActivity.class)
                            );

                            break;
                        }
                    }
                });
    }

    private void clearFlagKeepScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
