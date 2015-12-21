package io.mazur.fit.presenter;

import android.view.View;

import java.io.Serializable;

public abstract class IPresenter<V extends View> implements Serializable {
    protected transient V mView;

    public void onCreateView(V view) {
        mView = view;
    }

    public abstract void onSaveInstanceState();

    public abstract void onRestoreInstanceState();
}
