package io.mazur.fit.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarPresenter;

public class CalendarView extends LinearLayout {
    private CalendarPresenter mCalendarPresenter;

    @InjectView(R.id.layout) LinearLayout mLayout;

    LinearLayout mRowLayout;

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

        createRowLayout();
    }

    public void onCreate(int viewPagerPosition) {
        mCalendarPresenter = new CalendarPresenter(viewPagerPosition);
    }

    public void onCreateView() {
        mCalendarPresenter.onCreateView(this);
    }

    public CalendarPresenter getCalendarPresenter() {
        return mCalendarPresenter;
    }

    public LinearLayout getLayout() {
        return mLayout;
    }

    public void createRowLayout() {
        mRowLayout = (LinearLayout) LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_calendar_item_row, getLayout(), false);

        getLayout().addView(mRowLayout);
    }

    public LinearLayout getRowLayout() {
        return mRowLayout;
    }

    public void createDayView(int dayOfMonth, String colorString, boolean clickable) {
        View view = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_calendar_item_day, getRowLayout(), false);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);

        if(clickable) {
            view.setOnClickListener((v) -> {
                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                cardView.setRadius(76);

                dayTextView.setTextColor(Color.parseColor("#FF4081"));
            });
        }

        dayTextView.setTextColor(Color.parseColor(colorString));
        dayTextView.setText(String.valueOf(dayOfMonth));

        getRowLayout().addView(view);
    }

    public void createDayViewActive(int dayOfMonth) {
        View view = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_calendar_item_day, getRowLayout(), false);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(Color.parseColor("#3A3A46"));
        cardView.setRadius(76);

        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);
        dayTextView.setTextColor(Color.parseColor("#FFFFFF"));
        dayTextView.setText(String.valueOf(dayOfMonth));

        getRowLayout().addView(view);
    }

    public void createDayViewSelective(int dayOfMonth) {
        View view = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_calendar_item_day, getRowLayout(), false);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        cardView.setRadius(76);

        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);
        dayTextView.setTextColor(Color.parseColor("#FF4081"));
        dayTextView.setText(String.valueOf(dayOfMonth));

        getRowLayout().addView(view);
    }
}
