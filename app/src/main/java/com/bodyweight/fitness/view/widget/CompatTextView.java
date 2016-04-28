package com.bodyweight.fitness.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.bodyweight.fitness.R;

public class CompatTextView extends AppCompatTextView {
    public CompatTextView(Context context) {
        super(context);
        init(null);
    }

    public CompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            Context context = getContext();

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompatTextView);
            AppCompatDrawableManager dm = AppCompatDrawableManager.get();

            int startDrawableRes = a.getResourceId(R.styleable.CompatTextView_drawableStart, 0);

            Drawable[] currentDrawables = getCompoundDrawables();

            setCompoundDrawablesWithIntrinsicBounds(
                    dm.getDrawable(context, startDrawableRes),
                    currentDrawables[2],
                    currentDrawables[1],
                    currentDrawables[3]);

            a.recycle();
        }
    }
}