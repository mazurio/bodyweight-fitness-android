package io.mazur.fit.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

import io.mazur.fit.R;
import io.mazur.fit.adapter.ProgressAdapter;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.model.realm.RealmSet;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.stream.RoutineStream;
import io.realm.Realm;

public class ProgressActivity extends AppCompatActivity {
    private Realm mRealm;
    private RealmRoutine mRealmRoutine;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress);

        setToolbar();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Warmup"));
        tabLayout.addTab(tabLayout.newTab().setText("Skills Work"));
        tabLayout.addTab(tabLayout.newTab().setText("Strength Work"));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRealm = RealmStream.getInstance().getRealm();

        Date start = (Date) getIntent().getSerializableExtra("start");
        Date end = (Date) getIntent().getSerializableExtra("end");

        boolean exists = getIntent().getBooleanExtra("exists", false);

        mRealm = RealmStream.getInstance().getRealm();

        if(start != null && end != null) {
            if(exists) {
                mRealmRoutine = mRealm
                        .where(RealmRoutine.class)
                        .between("date", start, end)
                        .findFirst();

                if(mRealmRoutine != null) {
                    mRecyclerView.setAdapter(new ProgressAdapter(mRealmRoutine));
                }
            } else {
                mRealmRoutine = RealmStream.getInstance().buildRealmRoutineFrom(
                        RoutineStream.getInstance().getRoutine(), new DateTime(start).plusHours(1));

                mRecyclerView.setAdapter(new ProgressAdapter(mRealmRoutine));
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        mRealm.beginTransaction();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRealm.copyToRealmOrUpdate(mRealmRoutine);
        mRealm.commitTransaction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Workout");
            actionBar.setElevation(0);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
