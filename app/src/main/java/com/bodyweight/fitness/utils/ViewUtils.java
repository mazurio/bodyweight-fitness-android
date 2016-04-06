package com.bodyweight.fitness.utils;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
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
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) floatingActionButton.getLayoutParams();

            p.setMargins(0, 0, 0, 0);

            floatingActionButton.setLayoutParams(p);
        }
    }

    public static void resetFloatingActionButtonMargin(
            FloatingActionButton floatingActionButton,
            int left,
            int top,
            int right,
            int bottom
    ) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) floatingActionButton.getLayoutParams();

            p.setMargins(left, top, right, bottom);

            floatingActionButton.setLayoutParams(p);
        }
    }
}
