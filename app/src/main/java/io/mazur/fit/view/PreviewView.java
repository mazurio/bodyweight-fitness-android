package io.mazur.fit.view;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.gordonwong.materialsheetfab.DimOverlayFrameLayout;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.presenter.PreviewPresenter;
import io.mazur.fit.view.widget.Fab;
import pl.droidsonroids.gif.GifImageView;

public class PreviewView extends RelativeLayout {
    private PreviewPresenter mPreviewPresenter;

    @InjectView(R.id.preview_gif_image_view)
    GifImageView mPreviewGifImageView;

    @InjectView(R.id.log_workout_button)
    FloatingActionButton mLogWorkoutButton;

    @InjectView(R.id.action_button)
    Fab mActionButton;

    @InjectView(R.id.overlay)
    DimOverlayFrameLayout mOverlay;

    @InjectView(R.id.fab_sheet)
    CardView mFabSheet;

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

        mLogWorkoutButton.setOnClickListener((v) -> mPreviewPresenter.onLogWorkoutButtonClick());
        mActionButton.setOnClickListener((v) -> mPreviewPresenter.onActionButtonClick());

        MaterialSheetFab materialSheetFab = new MaterialSheetFab<>(mActionButton, mFabSheet, mOverlay,
                Color.parseColor("#FFFFFF"), Color.parseColor("#FFFDDD"));

        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();

                getLogWorkoutButton().setVisibility(View.GONE);
            }

            @Override
            public void onSheetShown() {
                super.onSheetShown();
            }

            @Override
            public void onHideSheet() {
                super.onHideSheet();
            }

            @Override
            public void onSheetHidden() {
                super.onSheetHidden();

                getLogWorkoutButton().setVisibility(View.VISIBLE);
            }
        });

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

    public FloatingActionButton getLogWorkoutButton() {
        return mLogWorkoutButton;
    }

    public FloatingActionButton getActionButton() {
        return mActionButton;
    }
}
