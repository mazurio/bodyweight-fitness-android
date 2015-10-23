package io.mazur.fit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import io.mazur.fit.presenter.CalendarPresenter;

public class CalendarView extends RelativeLayout {
    private CalendarPresenter mCalendarPresenter;

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

        onCreateView();
    }

    public void onCreate() {
        mCalendarPresenter = new CalendarPresenter();
    }

    public void onCreateView() {
        mCalendarPresenter.onCreateView(this);
    }
}
