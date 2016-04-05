package com.bodyweight.fitness.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.ui.DashboardActivity;

public class DashboardPagerAdapter extends PagerAdapter {
    private DashboardActivity mDashboardActivity;

    private Routine mRoutine;
    private Exercise mExercise;

    public DashboardPagerAdapter(DashboardActivity dashboardActivity, Routine routine, Exercise exercise) {
        mDashboardActivity = dashboardActivity;
        mRoutine = routine;
        mExercise = exercise;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final ViewPager viewPager = (ViewPager) viewGroup;

        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_dashboard_page,
                viewGroup,
                false);

        DashboardAdapter mDashboardAdapter;

        mDashboardAdapter = new DashboardAdapter(mRoutine, mExercise, position);
        mDashboardAdapter.setOnExerciseClickListener((exercise -> {
            RoutineStream.getInstance().setExercise(exercise);
            mDashboardActivity.supportFinishAfterTransition();
//            supportFinishAfterTransition();
        }));

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(viewGroup.getContext());

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.view_dashboard_list);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDashboardAdapter);

        viewPager.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        if (mRoutine != null) {
            return mRoutine.getCategories().size();
        }

        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mRoutine != null) {
            return mRoutine.getCategories().get(position).getTitle();
        }

        return "";
    }
}
