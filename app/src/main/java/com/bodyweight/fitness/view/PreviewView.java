package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bodyweight.fitness.presenter.PreviewPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import icepick.Icepick;
import icepick.State;
import com.bodyweight.fitness.R;

public class PreviewView extends RelativeLayout {
    @State
    PreviewPresenter mPresenter;

    @InjectView(R.id.preview_gif_image_view)
    ImageView mPreviewGifImageView;

    public PreviewView(Context context) {
        super(context);

        onCreate();
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        onCreateView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        mPresenter.onSaveInstanceState();

        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mPresenter.onDestroyView();
        mPresenter = null;

        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));

        mPresenter.onRestoreInstanceState(this);
    }

    public void onCreate() {
        mPresenter = new PreviewPresenter();
    }

    public void onCreateView() {
        mPresenter.onCreateView(this);
    }

    public ImageView getPreviewGifImageView() {
        return mPreviewGifImageView;
    }
}
