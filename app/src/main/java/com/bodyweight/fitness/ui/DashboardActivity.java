package com.bodyweight.fitness.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.DashboardAdapter;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DashboardActivity extends AppCompatActivity {
    private DashboardAdapter mDashboardAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;

    @InjectView(R.id.bottom_text)
    TextView mBottomText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        ButterKnife.inject(this);

        setToolbar();

        Routine currentRoutine = RoutineStream.getInstance().getRoutine();
        Exercise currentExercise = RoutineStream.getInstance().getExercise();

        mDashboardAdapter = new DashboardAdapter(currentRoutine, currentExercise);
        mDashboardAdapter.setOnExerciseClickListener((exercise -> {
            RoutineStream.getInstance().setExercise(exercise);

            supportFinishAfterTransition();
        }));

        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.view_dashboard_list);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDashboardAdapter);

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

        mBottomText.setText(String.format("Completed %s out of %s exercises", mCompletedExercises, mTotalExercises));
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

        int position = mDashboardAdapter.getScrollPosition();

        if (position > 0) {
            position -= 1;
        }

        mRecyclerView.scrollToPosition(position);
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
            actionBar.setTitle("Bodyweight Fitness");
            actionBar.setSubtitle("Recommended Routine");
            actionBar.setElevation(0);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
