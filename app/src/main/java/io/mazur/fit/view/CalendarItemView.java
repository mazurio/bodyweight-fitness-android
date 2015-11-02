package io.mazur.fit.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.presenter.CalendarItemPresenter;

public class CalendarItemView extends LinearLayout {
    private CalendarItemPresenter mCalendarItemPresenter;

    private View mViewCalendarActionButton;
    private View mViewCalendarDetails;

    @InjectView(R.id.layout) LinearLayout mLayout;
    private LinearLayout mRowLayout;

    private int mClickedPosition = -1;

    private View mClickedView;
    private TextView mClickedTextView;

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

        createRowLayout();
    }

    public void onCreate(int viewPagerPosition) {
        mCalendarItemPresenter = new CalendarItemPresenter(viewPagerPosition);
    }

    public void onCreateView(View viewCalendarActionButton, View viewCalendarDetails) {
        mViewCalendarActionButton = viewCalendarActionButton;
        mViewCalendarDetails = viewCalendarDetails;

        mCalendarItemPresenter.onCreateView(this);
    }

    public CalendarItemPresenter getCalendarItemPresenter() {
        return mCalendarItemPresenter;
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

    public void createDayView(int dayOfMonth, boolean clickable, boolean active) {
        View view;

        if(clickable) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.view_calendar_item_day, getRowLayout(), false);
        } else {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.view_calendar_item_empty, getRowLayout(), false);
        }

        view.setId(dayOfMonth);

        View roundLayout = view.findViewById(R.id.roundLayout);

        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);
        View dot = view.findViewById(R.id.dot);

        if(active) {
            roundLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.rounded_corner_today));

            dayTextView.setTextColor(Color.parseColor("#FFFFFF"));
            dayTextView.setText(String.valueOf(dayOfMonth));
        } else {
            dayTextView.setTextColor(Color.parseColor("#00453E"));
            dayTextView.setText(String.valueOf(dayOfMonth));
        }

        if(clickable) {
            if(mCalendarItemPresenter.isRoutineLogged(dayOfMonth)) {
                dot.setVisibility(View.VISIBLE);
            } else {
                dot.setVisibility(View.GONE);
            }

            view.setOnClickListener((v) -> {
                mCalendarItemPresenter.onDaySelected(dayOfMonth);

                if (mClickedPosition == dayOfMonth) {
                    return;
                }

                /**
                 * Reset currently clicked day. This could be done in much simpler and cleaner way
                 * later.
                 */
                if (mClickedView != null && mClickedTextView != null) {
                    /**
                     * Highlight today's date back.
                     */
                    if (mClickedPosition == mCalendarItemPresenter.getTodaysDayOfTheMonth()) {
                        mClickedView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.rounded_corner_today));
                        mClickedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    } else {
                        mClickedView.setBackgroundDrawable(null);
                        mClickedTextView.setTextColor(Color.parseColor("#00453E"));
                    }
                }

                roundLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.rounded_corner_active));

                dayTextView.setTextColor(Color.parseColor("#00453E"));

                mClickedPosition = dayOfMonth;
                mClickedView = roundLayout;
                mClickedTextView = dayTextView;
            });
        }

        getRowLayout().addView(view);
    }

    public View getCalendarActionButton() {
        return mViewCalendarActionButton;
    }

    public View getCalendarDetails() {
        return mViewCalendarDetails;
    }
}
