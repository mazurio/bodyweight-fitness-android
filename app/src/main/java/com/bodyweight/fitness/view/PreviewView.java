package com.bodyweight.fitness.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.presenter.PreviewPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.bodyweight.fitness.R;

public class PreviewView extends RelativeLayout {
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
            mPresenter = (PreviewPresenter) ((Bundle) state).getSerializable(Constants.PRESENTER_KEY);

            super.onRestoreInstanceState(((Bundle) state).getParcelable(Constants.SUPER_STATE_KEY));

            mPresenter.onRestoreInstanceState(this);
        }
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
