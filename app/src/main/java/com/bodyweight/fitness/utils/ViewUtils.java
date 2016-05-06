package com.bodyweight.fitness.utils;

import android.view.View;

public class ViewUtils {
    public static void setBackgroundResourceWithPadding(View view, int resource) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();

        view.setBackgroundResource(resource);
        view.setPadding(left, top, right, bottom);
    }
}
