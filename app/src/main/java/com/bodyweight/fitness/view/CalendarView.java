package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.adapter.CalendarListAdapter;
import com.bodyweight.fitness.presenter.CalendarPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.CalendarAdapter;
import com.bodyweight.fitness.view.widget.ViewPager;

public class CalendarView extends LinearLayout {
    CalendarPresenter mPresenter;

    @InjectView(R.id.view_calendar_pager)
    ViewPager mViewPager;

    @InjectView(R.id.view_calendar_list)
    RecyclerView mList;

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

        mList.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewPager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPresenter.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        onCreateView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        mPresenter.onSaveInstanceState();

        Bundle state = new Bundle();

        state.putParcelable(Constants.SUPER_STATE_KEY, super.onSaveInstanceState());
        state.putSerializable(Constants.PRESENTER_KEY, mPresenter);

        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mPresenter.onDestroyView();
        mPresenter = null;

        if (state instanceof Bundle) {
            mPresenter = (CalendarPresenter) ((Bundle) state).getSerializable(Constants.PRESENTER_KEY);

            super.onRestoreInstanceState(((Bundle) state).getParcelable(Constants.SUPER_STATE_KEY));

            mPresenter.onRestoreInstanceState(this);
        }
    }

    public void onCreate() {
        mPresenter = new CalendarPresenter();
    }

    public void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public void setAdapter(CalendarAdapter calendarAdapter) {
        mViewPager.setAdapter(calendarAdapter);
    }

    public void setListAdapter(CalendarListAdapter calendarListAdapter) {
        mList.setAdapter(calendarListAdapter);
    }

    public void scrollToDefaultItem() {
        mViewPager.setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false);
    }

    public void hideCardView() {
        mList.setVisibility(View.GONE);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void showCardView() {
        mList.setVisibility(View.VISIBLE);
        mMessage.setVisibility(View.GONE);
    }
}
