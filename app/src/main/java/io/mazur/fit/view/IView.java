package io.mazur.fit.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.Icicle;

import io.mazur.fit.presenter.IPresenter;

public abstract class IView <P extends IPresenter> extends RelativeLayout {
    @Icicle
    P mPresenter;

    public IView(Context context) {
        super(context);

        onCreate();
    }

    public IView(Context context, AttributeSet attrs) {
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
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));

        mPresenter.onRestoreInstanceState();
    }

    public abstract void onCreate();

    public void onCreateView() {
        mPresenter.onCreateView(this);
    }
}
