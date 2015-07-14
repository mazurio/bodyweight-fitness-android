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
import android.view.WindowManager;

import io.mazur.fit.R;
import io.mazur.fit.fragment.NavigationDrawerFragment;
import io.mazur.fit.model.ActivityState;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.SectionMode;
import io.mazur.fit.stream.ActivityStream;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.PreferenceUtil;
import io.mazur.fit.view.ProgressDialog;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Exercise mExercise;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setToolbar();
        setDrawer();

        RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
            mExercise = exercise;
            invalidateOptionsMenu();
        });

        keepScreenOnWhenAppIsRunning();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem progressionMenuItem = menu.findItem(R.id.action_progression);

        if(mExercise != null) {
            if(mExercise.getSection().getSectionMode() == SectionMode.LEVELS || mExercise.getSection().getSectionMode() == SectionMode.PICK) {
                progressionMenuItem.setVisible(true);
            } else {
                progressionMenuItem.setVisible(false);
            }
        } else {
            progressionMenuItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if(mNavigationDrawerFragment.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

		switch(item.getItemId()) {
            case(R.id.action_progression): {
                ProgressDialog progressDialog = new ProgressDialog(this, mExercise);
                progressDialog.show();

                return true;
            }

            case (R.id.action_faq): {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.reddit.com/r/bodyweightfitness/wiki/faq")));

                return true;
            }

			case (R.id.action_settings): {
                startActivity(new Intent(
                        getApplicationContext(),
                        SettingsActivity.class));

                return true;
            }
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
