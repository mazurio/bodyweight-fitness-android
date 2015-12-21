package io.mazur.fit.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import io.mazur.fit.R;
import io.mazur.fit.model.realm.RealmCategory;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmSection;
import io.mazur.fit.model.realm.RealmSet;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.utils.Logger;
import io.mazur.fit.view.dialog.LogWorkoutDialog;
import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.functions.Func1;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressPresenter>
        implements LogWorkoutDialog.OnDismissLogWorkoutDialogListener {
    private RealmCategory mRealmCategory;

    private HashMap<Integer, RealmSection> mItemViewMapping = new HashMap<>();
    private HashMap<Integer, RealmExercise> mExerciseViewMapping = new HashMap<>();

    private int mTotalSize = 0;

    public ProgressAdapter(RealmCategory realmCategory) {
        super();

        mRealmCategory = realmCategory;

        /**
         * We loop over the sections in order to find out the item view id
         * for each section in the Recycler View.
         */
        int sectionId = 0;
        int exerciseId = 1;
        for(RealmSection realmSection : mRealmCategory.getSections()) {
            mItemViewMapping.put(sectionId, realmSection);

            int numberOfExercises = 0;

            for(RealmExercise realmExercise : realmSection.getExercises()) {
                if(realmExercise.isVisible()) {
                    mExerciseViewMapping.put(exerciseId, realmExercise);

                    exerciseId += 1;
                    mTotalSize += 1;

                    numberOfExercises++;
                }
            }

            sectionId = sectionId + numberOfExercises + 1;
            exerciseId += 1;
            mTotalSize += 1;
        }
    }

    @Override
    public ProgressPresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            return new ProgressTitlePresenter(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.activity_progress_title, parent, false));
        }

        return new ProgressCardPresenter(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_progress_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ProgressPresenter holder, int position) {
        if (mItemViewMapping.containsKey(position)) {
            ProgressTitlePresenter presenter = (ProgressTitlePresenter) holder;
            presenter.bindView(mItemViewMapping.get(position));
        } else if (mExerciseViewMapping.containsKey(position)) {
            ProgressCardPresenter presenter = (ProgressCardPresenter) holder;
            presenter.bindView(mExerciseViewMapping.get(position), this);
        }
    }

    @Override
    public int getItemCount() {
        return mTotalSize;
    }

    @Override
    public int getItemViewType(int position) {
        if(mItemViewMapping.containsKey(position)) {
            return 1;
        }

        return 0;
    }

    @Override
    public void onDismissed() {
        notifyDataSetChanged();
    }

    public abstract class ProgressPresenter extends RecyclerView.ViewHolder {
        public ProgressPresenter(View itemView) {
            super(itemView);
        }
    }

    public class ProgressCardPresenter extends ProgressPresenter {
        @InjectView(R.id.toolbar) Toolbar mToolbar;
        @InjectView(R.id.chartLayout) RelativeLayout mChartLayout;
        @InjectView(R.id.chart1) LineChart mLineChart;
        @InjectView(R.id.view_button) Button mButton;

        private RealmExercise mRealmExercise;

        public ProgressCardPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void bindView(RealmExercise realmExercise, LogWorkoutDialog.OnDismissLogWorkoutDialogListener onDismissLogWorkoutDialogListener) {
            mRealmExercise = realmExercise;

            mToolbar.setTitle(realmExercise.getTitle());
            mToolbar.setSubtitle(realmExercise.getDescription());

            if(isCompleted(mRealmExercise)) {
                mChartLayout.setVisibility(View.VISIBLE);
                updateChart();
            } else {
                mChartLayout.setVisibility(View.GONE);
                mToolbar.setSubtitle("Not completed");
            }

            mButton.setOnClickListener(view -> {
                LogWorkoutDialog logWorkoutDialog = new LogWorkoutDialog(itemView.getContext(), mRealmExercise);
                logWorkoutDialog.setOnDismissLogWorkoutDialogListener(onDismissLogWorkoutDialogListener);
                logWorkoutDialog.show();
            });
        }

        private void updateChart() {
            boolean isTimed = mRealmExercise.getDefaultSet().equals("timed");

            ArrayList<Entry> valsComp1 = new ArrayList<>();
            ArrayList<String> xVals = new ArrayList<>();

            int index = 1;
            for(RealmSet realmSet : mRealmExercise.getSets()) {
                if (isTimed) {
                    int seconds = realmSet.getSeconds();

                    xVals.add(String.format("Set %d", index));
                    valsComp1.add(new Entry(seconds, (index - 1)));
                } else {
                    int reps = realmSet.getReps();

                    xVals.add(String.format("Set %d", index));

                    valsComp1.add(new Entry(reps, (index - 1)));
                }

                index++;
            }

            mLineChart.setDescription("");
            mLineChart.setDrawGridBackground(false);
            mLineChart.setDrawBorders(false);
            mLineChart.setBorderColor(Color.parseColor("#EAEAEA"));
            mLineChart.setBackgroundColor(Color.parseColor("#ffffff"));

            mLineChart.getLegend().setEnabled(false);
            mLineChart.getAxisRight().setEnabled(false);

            mLineChart.setGridBackgroundColor(Color.parseColor("#111111"));

            mLineChart.setExtraRightOffset(10f);

            mLineChart.setClickable(false);
            mLineChart.setTouchEnabled(false);
            mLineChart.setDragEnabled(false);
            mLineChart.setScaleEnabled(false);
            mLineChart.setPinchZoom(false);

            int fillColor = Color.parseColor("#00453E"); // #1194F6 darkBlue

            LineDataSet setComp1 = new LineDataSet(valsComp1, "Day 1");
            setComp1.setCircleColor(fillColor);
            setComp1.setColor(Color.parseColor("#ffffff"));
            setComp1.setHighLightColor(Color.parseColor("#ffffff"));
            setComp1.setCircleSize(6f);
            setComp1.setLineWidth(2f);
            setComp1.setColor(fillColor);
            setComp1.setDrawCubic(true);
            setComp1.setFillAlpha(50);
            setComp1.setFillColor(fillColor);

            setComp1.setDrawCircleHole(false);
            setComp1.setDrawFilled(true);
            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);


            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(setComp1);

            LineData data = new LineData(xVals, dataSets);
            // Values above circles.
            data.setDrawValues(false);

            Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                    itemView.getContext(),
                    RobotoTypefaceManager.Typeface.ROBOTO_REGULAR);

            XAxis xAxis = mLineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(12f);
            xAxis.setTextColor(Color.parseColor("#A1A1A1"));
            xAxis.setTypeface(typeface);
            // Space between chart and labels.
            xAxis.setYOffset(15f);
            xAxis.setAxisLineColor(Color.parseColor("#EAEAEA"));
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(true);

            YAxis yAxis = mLineChart.getAxisLeft();

            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxis.setTextSize(12f);
            yAxis.setTypeface(typeface);
            yAxis.setAxisLineColor(Color.parseColor("#FFFFFF"));

            // Space between chart and labels.
            yAxis.setXOffset(15f);
            yAxis.setTextColor(Color.parseColor("#A1A1A1"));
            yAxis.setGridColor(Color.parseColor("#EAEAEA"));
            yAxis.setAxisLineColor(Color.parseColor("#EAEAEA"));
            yAxis.setDrawAxisLine(true);
            yAxis.setDrawGridLines(true);
            yAxis.setYOffset(5f);

            mLineChart.setData(data);
            mLineChart.invalidate();
        }
    }

    public class ProgressTitlePresenter extends ProgressPresenter {
        @InjectView(R.id.title) TextView mTitle;

        public ProgressTitlePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void bindView(RealmSection realmSection) {
            mTitle.getPaddingTop();

            if (getLayoutPosition() == 0) {
                mTitle.setPadding(
                        mTitle.getPaddingLeft(),
                        mTitle.getPaddingLeft(),
                        mTitle.getPaddingRight(),
                        mTitle.getPaddingBottom()
                );
            } else {
                mTitle.setPadding(
                        mTitle.getPaddingLeft(),
                        mTitle.getPaddingBottom(),
                        mTitle.getPaddingRight(),
                        mTitle.getPaddingBottom()
                );
            }

            mTitle.setText(realmSection.getTitle());
        }
    }

    public boolean isCompleted(RealmExercise realmExercise) {
        int size = realmExercise.getSets().size();

        if (size == 0) {
            return false;
        }

        RealmSet firstSet = realmExercise.getSets().get(0);

        if(size == 1 && firstSet.getSeconds() == 0 && firstSet.getReps() == 0) {
            return false;
        }

        return true;
    }
}
