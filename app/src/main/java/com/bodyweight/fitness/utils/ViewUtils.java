package com.bodyweight.fitness.utils;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RelativeLayout;

public class ViewUtils {
    public static void setBackgroundResourceWithPadding(View view, int resource) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();

        view.setBackgroundResource(resource);
        view.setPadding(left, top, right, bottom);
    }

    public static void resetFloatingActionButtonMargin(FloatingActionButton floatingActionButton) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            floatingActionButton.setLayoutParams(layoutParams);
        }
    }
}
