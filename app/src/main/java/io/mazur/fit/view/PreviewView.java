package io.mazur.fit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.presenter.PreviewPresenter;

import pl.droidsonroids.gif.GifImageView;

public class PreviewView extends RelativeLayout {
    private PreviewPresenter mPreviewPresenter;

    @InjectView(R.id.preview_gif_image_view)
    GifImageView mPreviewGifImageView;

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

    public void onCreate() {
        mPreviewPresenter = new PreviewPresenter();
    }

    public void onCreateView() {
        mPreviewPresenter.onCreateView(this);
    }

    public GifImageView getPreviewGifImageView() {
        return mPreviewGifImageView;
    }
}
