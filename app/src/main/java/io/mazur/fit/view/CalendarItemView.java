package io.mazur.fit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

import butterknife.OnClick;
import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarItemPresenter;
import io.mazur.fit.utils.Logger;

public class CalendarItemView extends LinearLayout {
    private CalendarItemPresenter mCalendarItemPresenter;

    @InjectViews({
            R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4, R.id.day_5, R.id.day_6, R.id.day_7
    }) List<TextView> mDays;

    public CalendarItemView(Context context) {
        super(context);
    }

    public CalendarItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    public void onCreate(int viewPagerPosition) {
        mCalendarItemPresenter = new CalendarItemPresenter(viewPagerPosition);
    }

    public void onCreateView() {
        mCalendarItemPresenter.onCreateView(this);
    }

    public CalendarItemPresenter getCalendarItemPresenter() {
        return mCalendarItemPresenter;
    }

    public List<TextView> getDays() {
        return mDays;
    }

    @OnClick({
            R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4, R.id.day_5, R.id.day_6, R.id.day_7
    }) @SuppressWarnings("unused")
    public void onDaysClick(View view) {
        int indexOfObject = mDays.indexOf(view);

        Logger.d("" + indexOfObject);

        view.setBackgroundDrawable(
                getContext().getResources().getDrawable(
                        R.drawable.rounded_corner_today
                )
        );
    }
}
