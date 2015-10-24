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

    @InjectView(R.id.layout) LinearLayout mLayout;
    private LinearLayout mRowLayout;

    private int mClickedPosition = -1;

    private CardView mClickedCardView;
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

    public void onCreateView() {
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
        View view = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_calendar_item_day, getRowLayout(), false);

        view.setId(dayOfMonth);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);
        View dot = view.findViewById(R.id.dot);

        if(active) {
            cardView.setCardBackgroundColor(Color.parseColor("#3A3A46"));
            cardView.setRadius(76);

            dayTextView.setTextColor(Color.parseColor("#FFFFFF"));
            dayTextView.setText(String.valueOf(dayOfMonth));
        } else {
            dayTextView.setTextColor(Color.parseColor("#FFFFFF"));
            dayTextView.setText(String.valueOf(dayOfMonth));
        }

        if(mCalendarItemPresenter.isRoutineLogged(dayOfMonth)) {
            dot.setVisibility(View.VISIBLE);
        } else {
            dot.setVisibility(View.GONE);
        }

        if(clickable) {
            view.setOnClickListener((v) -> {
                mCalendarItemPresenter.onDaySelected(dayOfMonth);

                if (mClickedPosition == dayOfMonth) {
                    return;
                }

                /**
                 * Reset currently clicked day. This could be done in much simpler and cleaner way
                 * later.
                 */
                if (mClickedCardView != null && mClickedTextView != null) {
                    /**
                     * Highlight today's date back.
                     */
                    if (mClickedPosition == mCalendarItemPresenter.getTodaysDayOfTheMonth()) {
                        mClickedCardView.setCardBackgroundColor(Color.parseColor("#3A3A46"));
                        mClickedCardView.setRadius(76);
                        mClickedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    } else {
                        mClickedCardView.setCardBackgroundColor(Color.parseColor("#2E2E3B"));
                        mClickedCardView.setRadius(0);
                        mClickedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }

                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                cardView.setRadius(76);

                dayTextView.setTextColor(Color.parseColor("#FF4081"));

                mClickedPosition = dayOfMonth;
                mClickedCardView = cardView;
                mClickedTextView = dayTextView;
            });
        }

        getRowLayout().addView(view);
    }
}
