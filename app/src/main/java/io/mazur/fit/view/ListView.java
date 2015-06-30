package io.mazur.fit.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.presenter.ListPresenter;

public class ListView extends RelativeLayout {
    private ListPresenter mListPresenter;

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

    public ListView(Context context) {
        super(context);

        onCreate();
    }

    public ListView(Context context, AttributeSet attrs) {
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
        mListPresenter = new ListPresenter();
    }

    public void onCreateView() {
        mListPresenter.onCreateView(this);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
