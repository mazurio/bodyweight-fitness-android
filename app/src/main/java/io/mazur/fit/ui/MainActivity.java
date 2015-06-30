package io.mazur.fit.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import io.mazur.fit.R;
import io.mazur.fit.fragment.NavigationDrawerFragment;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.utils.Logger;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {
    private NavigationDrawerFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setToolbar();
        setDrawer();
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
	public boolean onOptionsItemSelected(MenuItem item) {
        if(mNavigationDrawerFragment.getActionBarDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

		switch(item.getItemId()) {
			case (R.id.action_settings):
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {
                actionBar.setTitle(exercise.getTitle());
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
}
