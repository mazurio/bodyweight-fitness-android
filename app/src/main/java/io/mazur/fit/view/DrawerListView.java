package io.mazur.fit.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.presenter.DrawerListPresenter;

public class DrawerListView extends RelativeLayout {
    private DrawerListPresenter mDrawerListPresenter;

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

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

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
