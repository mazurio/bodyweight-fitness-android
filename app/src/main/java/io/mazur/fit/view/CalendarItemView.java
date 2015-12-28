package io.mazur.fit.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;

import io.mazur.fit.R;
import io.mazur.fit.model.CalendarDayChanged;
import io.mazur.fit.presenter.CalendarItemPresenter;

import io.mazur.fit.stream.CalendarStream;
import io.mazur.fit.utils.ViewUtils;

public class CalendarItemView extends LinearLayout {
    private CalendarItemPresenter mCalendarItemPresenter;

    @InjectViews({
            R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4, R.id.day_5, R.id.day_6, R.id.day_7
    }) List<TextView> mDays;

    private TextView mClickedDay;

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

    public void onDestroyView() {
        mCalendarItemPresenter.onDestroyView();
    }

    public List<TextView> getDays() {
        return mDays;
    }

    @OnClick({
            R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4, R.id.day_5, R.id.day_6, R.id.day_7
    }) @SuppressWarnings("unused")
    public void onDaysClick(TextView view) {
        if(mClickedDay != null && mClickedDay != view) {
            unselectClickedDay();
        }

        view.setTextColor(Color.parseColor("#ffffff"));
        ViewUtils.setBackgroundResourceWithPadding(view, R.drawable.rounded_corner_today);

        CalendarDayChanged calendarDayChanged = new CalendarDayChanged();

        calendarDayChanged.daySelected = mDays.indexOf(view);
        calendarDayChanged.presenterSelected = mCalendarItemPresenter.getViewPagerPosition();

        CalendarStream.getInstance().setCalendarDay(calendarDayChanged);

        mClickedDay = view;
    }

    public void selectDay(int day) {
        TextView view = mDays.get(day);

        if(view != null) {
            onDaysClick(view);
        }
    }

    public void unselectClickedDay() {
        if(mClickedDay != null) {
            if(mClickedDay.getTag() != null && ((boolean) mClickedDay.getTag())) {
                mClickedDay.setTextColor(Color.parseColor("#00453E"));

                ViewUtils.setBackgroundResourceWithPadding(mClickedDay, R.drawable.rounded_corner_active);
            } else {
                mClickedDay.setTextColor(Color.parseColor("#00453E"));
                mClickedDay.setBackgroundResource(0);
            }
        }
    }
}
