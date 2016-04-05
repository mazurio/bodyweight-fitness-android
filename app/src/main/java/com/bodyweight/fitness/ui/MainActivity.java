package com.bodyweight.fitness.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bodyweight.fitness.stream.RepositoryStream;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.stream.Stream;
import com.bodyweight.fitness.utils.ApplicationStoreUtils;
import com.bodyweight.fitness.utils.PreferenceUtils;
import com.liulishuo.filedownloader.FileDownloader;

import rx.Subscription;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Subscription mSubscription;
    private Integer mId = R.id.action_menu_home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mId = savedInstanceState.getInt("ID");
        }

		setContentView(R.layout.activity_main);

        setToolbar();

        keepScreenOnWhenAppIsRunning();
    }

    @Override
    protected void onResume() {
        super.onResume();

        keepScreenOnWhenAppIsRunning();
        subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unsubscribe();
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
    protected void onDestroy() {
        super.onDestroy();

        FileDownloader.getImpl().pauseAll();
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

    private void keepScreenOnWhenAppIsRunning() {
        if(PreferenceUtils.getInstance().keepScreenOnWhenAppIsRunning()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            clearFlagKeepScreenOn();
        }
    }

    private void subscribe() {
        // TODO: REMOVE SUBSCRIPTIONS.
        Stream.INSTANCE.getMenuObservable()
                .filter(id -> id.equals(R.id.action_dashboard))
                .subscribe(id -> {
//                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            MainActivity.this,
//                            findViewById(R.id.start_stop_timer_button),
//                            "dashboard"
//                    );

                    startActivity(new Intent(this, DashboardActivity.class));
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

//                    ActivityCompat.startActivity(this,
//                            new Intent(this, DashboardActivity.class), activityOptionsCompat.toBundle());
                });


        // TODO: REMOVE SUBSCRIPTIONS.
        Stream.INSTANCE
                .getDrawerObservable()
                .filter(id ->
                        id.equals(R.id.action_menu_home) || id.equals(R.id.action_menu_workout_log))
                .subscribe(id -> {
                    mId = id;
                });

        mSubscription = Stream.INSTANCE
                .getDrawerObservable()
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

    private void unsubscribe() {
        mSubscription.unsubscribe();
    }

    private void clearFlagKeepScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
