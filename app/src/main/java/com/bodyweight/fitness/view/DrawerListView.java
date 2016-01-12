package com.bodyweight.fitness.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.bodyweight.fitness.adapter.DrawerListAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.presenter.DrawerListPresenter;

public class DrawerListView extends RelativeLayout {
    DrawerListPresenter mDrawerListPresenter;

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public DrawerListView(Context context) {
        super(context);

        onCreate();
    }

    public DrawerListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        onCreateView();
    }

    public void onCreate() {
        mDrawerListPresenter = new DrawerListPresenter();
    }

    public void onCreateView() {
        mDrawerListPresenter.onCreateView(this);
    }

    public void setAdapter(DrawerListAdapter drawerListAdapter) {
        mRecyclerView.setAdapter(drawerListAdapter);
    }
}
