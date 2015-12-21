package io.mazur.fit.view;

import android.content.Context;
import android.util.AttributeSet;

import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.presenter.PreviewPresenter;

import pl.droidsonroids.gif.GifImageView;

public class PreviewView extends IView <PreviewPresenter> {
    @InjectView(R.id.preview_gif_image_view) GifImageView mPreviewGifImageView;

    public PreviewView(Context context) {
        super(context);
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onCreate() {
        mPresenter = new PreviewPresenter();
    }

    public GifImageView getPreviewGifImageView() {
        return mPreviewGifImageView;
    }
}
