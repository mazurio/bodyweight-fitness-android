package io.mazur.fit.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarPresenter;
import io.mazur.fit.view.widget.ViewPager;

public class CalendarView extends LinearLayout {
    private CalendarPresenter mCalendarPresenter;

    @InjectView(R.id.view_calendar_pager)
    ViewPager mViewPager;

    @InjectView(R.id.view_calendar_date)
    TextView mDate;

    @InjectView(R.id.view_calendar_card)
    CardView mCardView;

    @InjectView(R.id.view_calendar_card_view_button)
    Button mViewButton;

    @InjectView(R.id.view_calendar_card_export_button)
    Button mExportButton;

    @InjectView(R.id.view_calendar_card_remove_button)
    Button mRemoveButton;

    @InjectView(R.id.view_calendar_message)
    View mMessage;

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

    public TextView getDate() {
        return mDate;
    }

    public CardView getCardView() {
        return mCardView;
    }

    public Button getViewButton() {
        return mViewButton;
    }

    public Button getExportButton() {
        return mExportButton;
    }

    public Button getRemoveButton() {
        return mRemoveButton;
    }

    public View getMessage() {
        return mMessage;
    }
}
