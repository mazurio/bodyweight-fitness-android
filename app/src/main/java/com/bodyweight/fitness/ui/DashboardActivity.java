package com.bodyweight.fitness.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.DashboardAdapter;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DashboardActivity extends AppCompatActivity {
    private DashboardAdapter mDashboardAdapter;

    @InjectView(R.id.view_dashboard_list)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        ButterKnife.inject(this);

        setToolbar();

        Routine routine = RoutineStream.getInstance().getRoutine();
        Exercise exercise = RoutineStream.getInstance().getExercise();

        mDashboardAdapter = new DashboardAdapter(routine, exercise);
        mDashboardAdapter.setOnExerciseClickListener((e -> {
            RoutineStream.getInstance().setExercise(e);

            supportFinishAfterTransition();
        }));

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDashboardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRecyclerView.scrollToPosition(mDashboardAdapter.getScrollPosition());
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
