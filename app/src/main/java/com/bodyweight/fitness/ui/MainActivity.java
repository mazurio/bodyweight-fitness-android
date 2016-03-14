package com.bodyweight.fitness.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.WindowManager;

import com.bodyweight.fitness.stream.RepositoryStream;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.stream.DrawerStream;
import com.bodyweight.fitness.stream.ToolbarStream;
import com.bodyweight.fitness.utils.ApplicationStoreUtils;
import com.bodyweight.fitness.view.fragment.NavigationDrawerFragment;
import com.bodyweight.fitness.utils.PreferenceUtils;

import rx.Subscription;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Subscription mSubscription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setToolbar();
        setDrawer();

        keepScreenOnWhenAppIsRunning();

        /**
         * TODO: Do this only once (unless it changes).
         *
         * Slide:
         * Slide: Log Workouts
         * Slide: Track your progress
         * Slide: Download more routines (Flexibility and Stretching)
         */
//        Intent intent = new Intent(this, SplashActivity.class);
//        startActivity(intent);
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
    protected void onPause() {
        super.onPause();

        unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();

        keepScreenOnWhenAppIsRunning();
        subscribe();
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
        if (mNavigationDrawerFragment.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        ToolbarStream.getInstance().setMenu(item.getItemId());

		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        keepScreenOnWhenAppIsRunning();
    }

    private void setToolbar() {
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
	}

    private void setDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer_fragment);
        mNavigationDrawerFragment.setDrawer(R.id.drawer_fragment, drawerLayout);
    }

    private void keepScreenOnWhenAppIsRunning() {
        if(PreferenceUtils.getInstance().keepScreenOnWhenAppIsRunning()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            clearFlagKeepScreenOn();
        }
    }

    private void subscribe() {
        mSubscription = DrawerStream.getInstance()
                .getMenuObservable()
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
