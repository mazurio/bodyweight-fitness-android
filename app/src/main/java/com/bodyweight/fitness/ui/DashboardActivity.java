package com.bodyweight.fitness.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.DashboardAdapter;
import com.bodyweight.fitness.adapter.DashboardPagerAdapter;
import com.bodyweight.fitness.model.Category;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.stream.RoutineStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DashboardActivity extends AppCompatActivity {
//    @InjectView(R.id.routine_title) TextView mRoutineTitle;
//    @InjectView(R.id.routine_subtitle) TextView mRoutineSubtitle;
//    @InjectView(R.id.completed_text) TextView mCompletedText;

//    @InjectView(R.id.view_dashboard_pager) ViewPager mViewPager;

    @InjectView(R.id.view_dashboard_list)
    RecyclerView mRecyclerView;

//    private DashboardPagerAdapter mDashboardPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        ButterKnife.inject(this);

        setToolbar();

        Routine routine = RoutineStream.getInstance().getRoutine();
        Exercise exercise = RoutineStream.getInstance().getExercise();

//        mRoutineTitle.setText(routine.getTitle());
//        mRoutineSubtitle.setText(routine.getSubtitle());



        DashboardAdapter mDashboardAdapter;

        mDashboardAdapter = new DashboardAdapter(routine, exercise);
        mDashboardAdapter.setOnExerciseClickListener((e -> {
            RoutineStream.getInstance().setExercise(e);
//            mDashboardActivity.supportFinishAfterTransition();
            supportFinishAfterTransition();
        }));

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDashboardAdapter);

//        mDashboardPagerAdapter = new DashboardPagerAdapter(this, routine, exercise);
//
//        mViewPager.setOffscreenPageLimit(4);
//        mViewPager.setAdapter(mDashboardPagerAdapter);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.setupWithViewPager(mViewPager);
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition(), true);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                if (tab.getPosition() != 0) {
////                    mDashboardPagerAdapter.onTabReselected(tab.getPosition());
//                }
//            }
//        });

        routine.getCategories();

        Category category = exercise.getCategory();

        int index = routine.getCategories().indexOf(category);

//        mViewPager.setCurrentItem(index);

        int mTotalExercises = 0;
        int mCompletedExercises = 0;
        for(RepositoryExercise repositoryExercise : RepositoryStream.getInstance().getRepositoryRoutineForToday().getExercises()) {
            if (repositoryExercise.isVisible()) {
                if (isCompleted(repositoryExercise)) {
                    mCompletedExercises++;
                }

                mTotalExercises++;
            }
        }

//        mCompletedText.setText(String.format("%s out of %s exercises", mCompletedExercises, mTotalExercises));
    }

    public boolean isCompleted(RepositoryExercise repositoryExercise) {
        int size = repositoryExercise.getSets().size();

        if (size == 0) {
            return false;
        }

        RepositorySet firstSet = repositoryExercise.getSets().get(0);

        if(size == 1 && firstSet.getSeconds() == 0 && firstSet.getReps() == 0) {
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mRecyclerView.scrollToPosition(mDashboardAdapter.getScrollPosition());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                supportFinishAfterTransition();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setSubtitle("");
            actionBar.setElevation(0);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
