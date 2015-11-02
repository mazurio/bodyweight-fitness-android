package io.mazur.fit.ui;

import android.os.Bundle;
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

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRealm = RealmStream.getInstance().getRealm();

        mRealm.beginTransaction();

        mRealmRoutine = mRealm.createObject(RealmRoutine.class);

        mRealmRoutine.setId("Routine-" + UUID.randomUUID().toString());
        mRealmRoutine.setDate(new DateTime().plusHours(1).toDate());

        addExercise(mRealmRoutine, "Wall Extensions", "1x5-10", 1, "Dynamic Stretches", 0);
        addExercise(mRealmRoutine, "Band Dislocates", "1x5-10", 1, "Dynamic Stretches", 1);
        addExercise(mRealmRoutine, "Cat/Camel Bends", "1x5-10", 1, "Dynamic Stretches", 2);
        addExercise(mRealmRoutine, "Scapular Shrugs", "1x5-10", 1, "Dynamic Stretches", 3);
        addExercise(mRealmRoutine, "Full Body Circles", "1x5-10", 1, "Dynamic Stretches", 4);
        addExercise(mRealmRoutine, "Front and Side Leg Swings", "1x5-10", 1, "Dynamic Stretches", 5);
        addExercise(mRealmRoutine, "Wrist Mobility Exercises", "1x5-10", 1, "Dynamic Stretches", 6);

        addExercise(mRealmRoutine, "Plank", "1x10-60s hold each", 1, "Bodyline Drills", 0);
        addExercise(mRealmRoutine, "Side Plank (Both Sides)", "1x10-60s hold each", 1, "Bodyline Drills", 1);
        addExercise(mRealmRoutine, "Reverse Plank", "1x10-60s hold each", 1, "Bodyline Drills", 2);
        addExercise(mRealmRoutine, "Hollow Hold", "1x10-60s hold each", 1, "Bodyline Drills", 3);
        addExercise(mRealmRoutine, "Arch", "1x10-60s hold each", 1, "Bodyline Drills", 4);

        addExercise(mRealmRoutine, "Burpees", "1x10-20", 1, "Activity", 0);
        addExercise(mRealmRoutine, "Wall Plank", "5 min", 1, "Handstand", 0);
        addExercise(mRealmRoutine, "Parallel Bar Support", "2-3 min", 2, "Support Practice", 0);
        addExercise(mRealmRoutine, "Pull Up", "3x5-8", 3, "Pullup Progression", 0);
        addExercise(mRealmRoutine, "Parallel Bar Dips", "3x5-8", 3, "Dipping Progression", 0);
        addExercise(mRealmRoutine, "Bodyweight Squat", "3x5-8", 3, "Squat Progression", 0);
        addExercise(mRealmRoutine, "L-Sit", "3x10-30s", 3, "L-Sit Progression", 0);
        addExercise(mRealmRoutine, "Pushup", "3x5-8", 3, "Pushing", 0);
        addExercise(mRealmRoutine, "Incline Row", "3x5-8", 3, "Row Progression", 0);

        mRealm.commitTransaction();

        mRecyclerView.setAdapter(new ProgressAdapter(mRealmRoutine));

//        Date start = (Date) getIntent().getSerializableExtra("start");
//        Date end = (Date) getIntent().getSerializableExtra("end");
//
//        boolean exists = getIntent().getBooleanExtra("exists", false);
//
//        mRealm = RealmStream.getInstance().getRealm();
//
//        if(start != null && end != null) {
//            if(exists) {
//                mRealmRoutine = mRealm
//                        .where(RealmRoutine.class)
//                        .between("date", start, end)
//                        .findFirst();
//
//                if(mRealmRoutine != null) {
//                    mRecyclerView.setAdapter(new ProgressAdapter(mRealmRoutine));
//                }
//            } else {
//                mRealmRoutine = RealmStream.getInstance().buildRealmRoutineFrom(
//                        RoutineStream.getInstance().getRoutine(), new DateTime(start).plusHours(1));
//
//                mRecyclerView.setAdapter(new ProgressAdapter(mRealmRoutine));
//            }
//        }
    }

    public void addExercise(RealmRoutine realmRoutine, String title, String description, int defaultNumberOfSets, String section, int sectionOrder) {
        RealmExercise realmExercise = mRealm.createObject(RealmExercise.class);
        realmExercise.setId("Exercise-" + UUID.randomUUID().toString());
        realmExercise.setTitle(title);
        realmExercise.setDescription(description);
        realmExercise.setSection(section);
        realmExercise.setSectionOrder(sectionOrder);

        for(int i = 0; i < defaultNumberOfSets; i++) {
            RealmSet realmSet = new RealmSet();
            realmSet.setId(UUID.randomUUID().toString());
            realmSet.setValue(0);

            realmExercise.getSets().add(realmSet);
        }

        realmRoutine.getExercises().add(realmExercise);
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
