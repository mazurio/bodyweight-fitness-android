package io.mazur.fit.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

import icepick.Icepick;
import icepick.State;

import io.mazur.fit.R;
import io.mazur.fit.presenter.DrawerListPresenter;
import io.mazur.fit.utils.Logger;

public class DrawerListView extends RelativeLayout {
    @State
    DrawerListPresenter mDrawerListPresenter;

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

        onCreateView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        mDrawerListPresenter.onSaveInstanceState();

        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mDrawerListPresenter.onDestroyView();
        mDrawerListPresenter = null;

        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));

        mDrawerListPresenter.onRestoreInstanceState(this);
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
