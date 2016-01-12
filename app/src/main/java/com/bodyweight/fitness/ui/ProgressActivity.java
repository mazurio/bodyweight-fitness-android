package com.bodyweight.fitness.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bodyweight.fitness.adapter.ProgressPagerAdapter;
import com.bodyweight.fitness.stream.RepositoryStream;

import org.joda.time.DateTime;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;

import io.realm.Realm;

public class ProgressActivity extends AppCompatActivity {
    @InjectView(R.id.view_progress_pager)
    ViewPager mViewPager;

    private RepositoryRoutine mRepositoryRoutine;
    private ProgressPagerAdapter mProgressPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress);

        ButterKnife.inject(this);

        String routineId = getIntent().getStringExtra("routineId");

        buildRoutine(routineId);

        setPager(mRepositoryRoutine);

        setToolbar();
        setTabLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildRoutine(String routineId) {
        Realm realm = RepositoryStream.getInstance().getRealm();

        mRepositoryRoutine = realm.where(RepositoryRoutine.class)
                .equalTo("id", routineId)
                .findFirst();
    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(new DateTime(mRepositoryRoutine.getStartTime()).toString("d MMMM", Locale.ENGLISH));
            actionBar.setElevation(0);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() != 0) {
                    mProgressPagerAdapter.onTabReselected(tab.getPosition());
                }
            }
        });
    }

    private void setPager(RepositoryRoutine repositoryRoutine) {
        mProgressPagerAdapter = new ProgressPagerAdapter(repositoryRoutine);

        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mProgressPagerAdapter);
    }
}
