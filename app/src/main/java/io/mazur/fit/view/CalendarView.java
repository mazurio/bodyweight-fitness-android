package io.mazur.fit.view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarPresenter;

public class CalendarView extends LinearLayout {
    private CalendarPresenter mCalendarPresenter;

    @InjectView(R.id.view_calendar_pager) ViewPager mViewPager;
    @InjectView(R.id.view_calendar_action_button) FloatingActionButton mViewCalendarActionButton;
    @InjectView(R.id.view_calendar_details) View mViewCalendarDetails;
    @InjectView(R.id.view_calendar_details_toolbar) Toolbar mViewCalendarDetailsToolbar;

    public CalendarView(Context context) {
        super(context);

        onCreate();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        onCreate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        onCreateView();
    }

    public void onCreate() {
        mCalendarPresenter = new CalendarPresenter();
    }

    public void onCreateView() {
        mCalendarPresenter.onCreateView(this);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public FloatingActionButton getViewCalendarActionButton() {
        return mViewCalendarActionButton;
    }

    public View getViewCalendarDetails() {
        return mViewCalendarDetails;
    }

    public Toolbar getViewCalendarDetailsToolbar() {
        return mViewCalendarDetailsToolbar;
    }
}
