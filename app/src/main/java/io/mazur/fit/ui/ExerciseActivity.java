package io.mazur.fit.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.CircularProgressBar;

/**
 * TODO: This is unused at the moment. Was meant to be used for changing levels.
 */
public class ExerciseActivity extends AppCompatActivity {
    @InjectView(R.id.tabLayout) TabLayout mTabLayout;
    @InjectView(R.id.levelProgressBar) CircularProgressBar mLevelProgressBar;

    private ObjectAnimator mProgressBarAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise);

        ButterKnife.inject(this);

        setToolbar();
        setTabBar();

        mLevelProgressBar.setWheelSize(12);
        mLevelProgressBar.setProgressColor(Color.parseColor("#00453E"));
        mLevelProgressBar.setProgressBackgroundColor(Color.parseColor("#009688"));

        if (this.mProgressBarAnimator != null) {
            this.mProgressBarAnimator.cancel();
        }

        animate(mLevelProgressBar, null, 1f, 1500);

        RoutineStream.getInstance().getExerciseObservable().subscribe(exercise -> {

        });
    }

    private void animate(final CircularProgressBar progressBar, final Animator.AnimatorListener listener, final float progress, final int duration) {

        this.mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        this.mProgressBarAnimator.setDuration(duration);

        this.mProgressBarAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationCancel(final Animator animation) {
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
//                progressBar.setProgress(progress);
//                progressBar.setProgress(0f);
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
            }

            @Override
            public void onAnimationStart(final Animator animation) {
            }
        });
        if (listener != null) {
            this.mProgressBarAnimator.addListener(listener);
        }
        this.mProgressBarAnimator.reverse();
        this.mProgressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        this.mProgressBarAnimator.setRepeatCount(0);
        this.mProgressBarAnimator.start();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
                actionBar.setSubtitle(exercise.getDescription());
            });

            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setTabBar() {
        mTabLayout.addTab(mTabLayout.newTab()
                .setText("About"));
    }
}
