package com.bodyweight.fitness.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.bodyweight.fitness.R;

public class CircularProgressBar extends View {
    private static final String INSTANCE_STATE_SAVEDSTATE = "saved_state";
    private static final String INSTANCE_STATE_PROGRESS = "progress";
    private static final String INSTANCE_STATE_MARKER_PROGRESS = "marker_progress";
    private static final String INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR = "progress_background_color";
    private static final String INSTANCE_STATE_PROGRESS_COLOR = "progress_color";
    private static final String INSTANCE_STATE_THUMB_VISIBLE = "thumb_visible";
    private static final String INSTANCE_STATE_MARKER_VISIBLE = "marker_visible";

    private final RectF mCircleBounds = new RectF();
    private Paint mBackgroundColorPaint = new Paint();

    private int mCircleStrokeWidth = 10;
    private int mGravity = Gravity.CENTER;
    private int mHorizontalInset = 0;

    private boolean mIsInitializing = true;
    private boolean mIsThumbEnabled = true;

    private Paint mMarkerColorPaint;
    private Paint mProgressColorPaint;
    private Paint mThumbColorPaint = new Paint();

    private float mMarkerProgress = 0.0f;
    private boolean mOverrdraw = false;
    private float mProgress = 0.3f;
    private int mProgressBackgroundColor;
    private int mProgressColor;

    private float mRadius;
    private float mThumbPosX;
    private float mThumbPosY;
    private int mThumbRadius = 20;
    private float mTranslationOffsetX;
    private float mTranslationOffsetY;
    private int mVerticalInset = 0;

    public CircularProgressBar(final Context context) {
        this(context, null);
    }

    public CircularProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.CircularProgressBarStyle);
    }

    public CircularProgressBar(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray attributes = context
                .obtainStyledAttributes(attrs, R.styleable.CircularProgressBar,
                        defStyle, 0);
        if (attributes != null)
        {
            try
            {
                setProgressColor(attributes.getColor(R.styleable.CircularProgressBar_progress_color, Color.CYAN));
                setProgressBackgroundColor(attributes.getColor(R.styleable.CircularProgressBar_progress_background_color,Color.GREEN));
                setProgress(attributes.getFloat(R.styleable.CircularProgressBar_progress, 0.0f));
                setWheelSize((int) attributes.getDimension(R.styleable.CircularProgressBar_stroke_width, 10));
                setThumbEnabled(attributes.getBoolean(R.styleable.CircularProgressBar_thumb_visible, true));

                mGravity = attributes.getInt(R.styleable.CircularProgressBar_android_gravity,Gravity.CENTER);
            }
            finally
            {
                attributes.recycle();
            }
        }

        mThumbRadius = mCircleStrokeWidth * 2;
        updateBackgroundColor();
        updateMarkerColor();
        updateProgressColor();

        // the view has now all properties and can be drawn
        mIsInitializing = false;

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.translate(mTranslationOffsetX, mTranslationOffsetY);
        final float progressRotation = getCurrentRotation();
        // draw the background
        if (!mOverrdraw)
        {
            canvas.drawArc(mCircleBounds, 270, -(360 - progressRotation), false,mBackgroundColorPaint);
        }
        // draw the progress or a full circle if overdraw is true
        canvas.drawArc(mCircleBounds, 270, mOverrdraw ? 360 : progressRotation, false,mProgressColorPaint);
        // draw the marker at the correct rotated position
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom(),heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);

        final int diameter;
        if (heightMeasureSpec == MeasureSpec.UNSPECIFIED)
        {
            // ScrollView
            diameter = width;
            computeInsets(0, 0);
        }
        else if (widthMeasureSpec == MeasureSpec.UNSPECIFIED)
        {
            // HorizontalScrollView
            diameter = height;
            computeInsets(0, 0);
        }
        else
        {
            // Default
            diameter = Math.min(width, height);
            computeInsets(width - diameter, height - diameter);
        }

        setMeasuredDimension(diameter, diameter);

        final float halfWidth = diameter * 0.5f;
        // width of the drawed circle (+ the drawedThumb)
        final float drawedWith;
        if (isThumbEnabled())
        {
            drawedWith = mThumbRadius * (5f / 6f);
        }
        else
        {
            drawedWith = mCircleStrokeWidth / 2f;
        }
        // -0.5f for pixel perfect fit inside the viewbounds
        mRadius = halfWidth - drawedWith - 0.5f;

        mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius);

        mThumbPosX = (float) (mRadius * Math.cos(0));
        mThumbPosY = (float) (mRadius * Math.sin(0));

        mTranslationOffsetX = halfWidth + mHorizontalInset;
        mTranslationOffsetY = halfWidth + mVerticalInset;

    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            setProgress(bundle.getFloat(INSTANCE_STATE_PROGRESS));

            final int progressColor = bundle.getInt(INSTANCE_STATE_PROGRESS_COLOR);
            if (progressColor != mProgressColor) {
                mProgressColor = progressColor;
                updateProgressColor();
            }

            final int progressBackgroundColor = bundle
                    .getInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR);
            if (progressBackgroundColor != mProgressBackgroundColor) {
                mProgressBackgroundColor = progressBackgroundColor;
                updateBackgroundColor();
            }

            mIsThumbEnabled = bundle.getBoolean(INSTANCE_STATE_THUMB_VISIBLE);


            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE_SAVEDSTATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE_SAVEDSTATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STATE_PROGRESS, mProgress);
        bundle.putFloat(INSTANCE_STATE_MARKER_PROGRESS, mMarkerProgress);
        bundle.putInt(INSTANCE_STATE_PROGRESS_COLOR, mProgressColor);
        bundle.putInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR, mProgressBackgroundColor);
        bundle.putBoolean(INSTANCE_STATE_THUMB_VISIBLE, mIsThumbEnabled);
        return bundle;
    }

    public boolean isThumbEnabled() {
        return mIsThumbEnabled;
    }

    public void setProgress(final float progress) {
        if (progress == mProgress) {
            mProgress = 0.3f;
        }

        if (progress == 1) {
            mOverrdraw = false;
            mProgress = 1;
        } else {

            if (progress >= 1) {
                mOverrdraw = true;
            } else {
                mOverrdraw = false;
            }

            mProgress = progress % 1.0f;
        }

        if (!mIsInitializing) {
            invalidate();
        }
    }

    public void setProgressBackgroundColor(final int color) {
        mProgressBackgroundColor = color;

        updateMarkerColor();
        updateBackgroundColor();
    }

    public void setProgressColor(final int color) {
        mProgressColor = color;

        updateProgressColor();
    }

    public void setThumbEnabled(final boolean enabled) {
        mIsThumbEnabled = enabled;
    }


    public void setWheelSize(final int dimension) {
        mCircleStrokeWidth = dimension;

        // update the paints
        updateBackgroundColor();
        updateMarkerColor();
        updateProgressColor();
    }

    @SuppressLint("NewApi")
    private void computeInsets(final int dx, final int dy) {
        int absoluteGravity = mGravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            absoluteGravity = Gravity.getAbsoluteGravity(mGravity, getLayoutDirection());
        }

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                mHorizontalInset = 0;
                break;
            case Gravity.RIGHT:
                mHorizontalInset = dx;
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                mHorizontalInset = dx / 2;
                break;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                mVerticalInset = 0;
                break;
            case Gravity.BOTTOM:
                mVerticalInset = dy;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                mVerticalInset = dy / 2;
                break;
        }
    }

    private float getCurrentRotation() {
        return 360 * mProgress;
    }

    private float getMarkerRotation() {
        return 360 * mMarkerProgress;
    }

    private void updateBackgroundColor() {
        mBackgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundColorPaint.setColor(mProgressBackgroundColor);
        mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
        mBackgroundColorPaint.setStrokeWidth(mCircleStrokeWidth);

        invalidate();
    }


    private void updateMarkerColor() {
        mMarkerColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkerColorPaint.setColor(mProgressBackgroundColor);
        mMarkerColorPaint.setStyle(Paint.Style.STROKE);
        mMarkerColorPaint.setStrokeWidth(mCircleStrokeWidth / 2);

        invalidate();
    }

    private void updateProgressColor() {
        mProgressColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressColorPaint.setColor(mProgressColor);
        mProgressColorPaint.setStyle(Paint.Style.STROKE);
        mProgressColorPaint.setStrokeWidth(mCircleStrokeWidth);

        mThumbColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbColorPaint.setColor(mProgressColor);
        mThumbColorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mThumbColorPaint.setStrokeWidth(mCircleStrokeWidth);

        invalidate();
    }


}
