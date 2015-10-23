package io.mazur.fit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarPresenter;

public class CalendarView extends LinearLayout {
    private CalendarPresenter mCalendarPresenter;

    @InjectView(R.id.layout) LinearLayout mLayout;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    public void onCreate(int viewPagerPosition) {
        mCalendarPresenter = new CalendarPresenter(viewPagerPosition);
    }

    public void onCreateView() {
        mCalendarPresenter.onCreateView(this);
    }

    public LinearLayout getLayout() {
        return mLayout;
    }
}
