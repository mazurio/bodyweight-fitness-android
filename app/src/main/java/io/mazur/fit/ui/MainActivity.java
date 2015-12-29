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

import android.view.MenuItem;
import android.view.WindowManager;

import io.mazur.fit.R;
import io.mazur.fit.stream.DrawerStream;
import io.mazur.fit.stream.RepositoryStream;
import io.mazur.fit.stream.ToolbarStream;
import io.mazur.fit.utils.ApplicationStoreUtils;
import io.mazur.fit.view.fragment.NavigationDrawerFragment;
import io.mazur.fit.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setToolbar();
        setDrawer();

        keepScreenOnWhenAppIsRunning();
        subscribe();
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
        DrawerStream.getInstance()
                .getMenuObservable()
                .subscribe(id -> {
                    switch (id) {
                        case (R.id.action_menu_faq): {
                            startActivity(
                                    new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.reddit.com/r/bodyweightfitness/wiki/faq"))
                            );

                            break;
                        }

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
