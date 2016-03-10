package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bodyweight.fitness.presenter.ToolbarPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import icepick.Icepick;
import icepick.State;

import com.bodyweight.fitness.R;

public class ToolbarView extends AppBarLayout {
    @State
    ToolbarPresenter mPresenter;

    @InjectView(R.id.toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.toolbar_layout)
    View mLayout;

    @InjectView(R.id.toolbar_exercise_title)
    TextView mTitle;

    @InjectView(R.id.toolbar_section_title)
    TextView mSubtitle;

    @InjectView(R.id.toolbar_exercise_description)
    TextView mDescription;

    public ToolbarView(Context context) {
        super(context);

        onCreate();
    }

    public ToolbarView(Context context, AttributeSet attrs) {
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
        mPresenter.onSaveInstanceState();

        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mPresenter.onDestroyView();
        mPresenter = null;

        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));

        mPresenter.onRestoreInstanceState(this);
    }

    public void onCreate() {
        mPresenter = new ToolbarPresenter();
    }

    public void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public void invalidateOptionsMenu() {
        mToolbar.getMenu().clear();
    }

    public void inflateCalendarMenu() {
        invalidateOptionsMenu();

        mToolbar.inflateMenu(R.menu.calendar);
    }

    public void setSingleTitle(String text) {
        mLayout.setVisibility(View.GONE);

        mToolbar.setTitle(text);
    }

    public void setTitle(String text) {
        mLayout.setVisibility(View.VISIBLE);

        mTitle.setText(text);
    }

    public void setSubtitle(String text) {
        mLayout.setVisibility(View.VISIBLE);

        mSubtitle.setText(text);
    }

    public void setDescription(String text) {
        mLayout.setVisibility(View.VISIBLE);

        mDescription.setText(text);
    }
}
