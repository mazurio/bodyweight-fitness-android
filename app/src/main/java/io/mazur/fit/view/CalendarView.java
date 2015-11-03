package io.mazur.fit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarPresenter;
import io.mazur.fit.view.widget.ViewPager;

public class CalendarView extends LinearLayout {
    private CalendarPresenter mCalendarPresenter;

    @InjectView(R.id.view_calendar_pager)
    ViewPager mViewPager;

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
}
