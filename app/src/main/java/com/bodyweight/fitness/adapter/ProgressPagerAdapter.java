package com.bodyweight.fitness.adapter;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodyweight.fitness.model.repository.RepositorySet;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.HashMap;
import java.util.Locale;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.repository.RepositoryCategory;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;

public class ProgressPagerAdapter extends PagerAdapter {
    private DecoView mDecoView;
    private int mBackIndex;
    private int mSeries1Index;

    private int mNumberOfExercises;
    private int mCurrentExercise;

    private RepositoryRoutine mRepositoryRoutine;
    private HashMap<Integer, RecyclerView> mRecyclerViewMap = new HashMap<>();

    public ProgressPagerAdapter(RepositoryRoutine repositoryRoutine) {
        super();

        mRepositoryRoutine = repositoryRoutine;

        for (RepositoryExercise repositoryExercise : repositoryRoutine.getExercises()) {
            if (repositoryExercise.isVisible()) {
                mNumberOfExercises++;

                if (isCompleted(repositoryExercise)) {
                    mCurrentExercise++;
                }
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        View view;

        if (position == 0) {
            view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.activity_progress_info, viewGroup, false);

            createStartTimeEndTime(view);
            createDecoView(view);
        } else {
            view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.activity_progress_page, viewGroup, false);

            createRecyclerView(view, position);
        }

        viewPager.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) object);

        mRecyclerViewMap.remove(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mRepositoryRoutine.getCategories().size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "General";
            default:
                return mRepositoryRoutine.getCategories().get(position - 1).getTitle();
        }
    }

    public void onTabReselected(int tabPosition) {
        mRecyclerViewMap.get(tabPosition).smoothScrollToPosition(0);
    }

    private void createStartTimeEndTime(View view) {
        TextView startTimeText = (TextView) view.findViewById(R.id.start_time_text);
        TextView endTimeLabel = (TextView) view.findViewById(R.id.end_time_label);
        TextView endTimeText = (TextView) view.findViewById(R.id.end_time_text);
        TextView workoutLengthText = (TextView) view.findViewById(R.id.workout_length_text);

        DateTime startTime = new DateTime(mRepositoryRoutine.getStartTime());
        DateTime lastUpdatedTime = new DateTime(mRepositoryRoutine.getLastUpdatedTime());

        Duration duration = new Duration(startTime, lastUpdatedTime);

        startTimeText.setText(
                startTime.toString("HH:mm", Locale.ENGLISH)
        );

        endTimeText.setText(
                lastUpdatedTime.toString("HH:mm", Locale.ENGLISH)
        );

        if (duration.toStandardMinutes().getMinutes() < 10) {
            workoutLengthText.setText("--");
        } else {
            String formatted = new PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix("h ")
                    .appendMinutes()
                    .appendSuffix("m")
                    .toFormatter()
                    .print(duration.toPeriod());

            workoutLengthText.setText(formatted);
        }

        if (mCurrentExercise == mNumberOfExercises) {
            endTimeLabel.setText("End Time");
        } else {
            endTimeLabel.setText("Last Updated");
        }
    }

    private void createRecyclerView(View view, int position) {
        RepositoryCategory repositoryCategory = mRepositoryRoutine.getCategories().get(position - 1);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new ProgressAdapter(repositoryCategory));

        mRecyclerViewMap.put(position, recyclerView);
    }

    /**
     * Methods below could be cleaned up with a graph presenter.
     */
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

    private void createDecoView(View view) {
        mDecoView = (DecoView) view.findViewById(R.id.dynamicArcView);

        createBackSeries();
        createDataSeries1(view);

        createEvents(view);
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mNumberOfExercises, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries1(View view) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#0B9394"))
                .setRange(0, mNumberOfExercises, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) view.findViewById(R.id.textPercentage);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));

                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textToGo = (TextView) view.findViewById(R.id.textRemaining);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                int remainingExercises = (int) (seriesItem.getMaxValue() - currentPosition);

                if(remainingExercises > 0) {
                    textToGo.setText(String.format("%d exercises to go", remainingExercises));
                } else {
                    textToGo.setText("Congratulations!");
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = mDecoView.addSeries(seriesItem);
    }

    private void createEvents(View view) {
        mDecoView.executeReset();

        mDecoView.addEvent(new DecoEvent.Builder(mNumberOfExercises)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mSeries1Index)
                .setDuration(2000)
                .setDelay(1250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(mCurrentExercise)
                .setIndex(mSeries1Index)
                .setDelay(3250)
                .build());

        resetText(view);
    }

    private void resetText(View view) {
        ((TextView) view.findViewById(R.id.textPercentage)).setText("");
        ((TextView) view.findViewById(R.id.textRemaining)).setText("");
    }
}
