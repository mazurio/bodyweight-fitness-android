package com.bodyweight.fitness.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class StillViewPager extends android.support.v4.view.ViewPager {
    private boolean isPagingEnabled = false;

    public StillViewPager(Context context) {
        super(context);
    }

    public StillViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }
}