package com.bodyweight.fitness.presenter;

import android.content.Context;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Subscription;

public abstract class IPresenter<V extends View> implements Serializable {
    /**
     * All subscriptions, views and adapters must
     * be transient so they are not serialized.
     */
    protected transient V mView;

    private transient ArrayList<Subscription> mSubscriptions = new ArrayList<>();

    public Context getContext() {
        return mView.getContext();
    }

    /**
     * Create new view.
     *
     * Remember to give view an unique id in an XML file.
     *
     * @param view view.
     */
    public void onCreateView(V view) {
        mView = view;

        onSubscribe();
    }

    /**
     * Destroy view and remove all subscriptions.
     */
    public void onDestroyView() {
        mView = null;

        unsubscribeAll();
    }

    /**
     * We can't call unsubscribeAll here as Activity calls
     * onSaveInstanceState with onPause method.
     */
    public void onSaveInstanceState() {}

    /**
     * Restore instance state with the new view being created.
     *
     * @param view new instance of the view.
     */
    public void onRestoreInstanceState(V view) {
        unsubscribeAll();

        mView = view;

        onSubscribe();
    }

    /**
     * Called by onCreateView and onRestoreInstanceState so we can recreate subscriptions.
     */
    public void onSubscribe() {}

    /**
     * Add transient Rx Subscription to the array list.
     *
     * @param subscription subscription.
     */
    public void subscribe(Subscription subscription) {
        mSubscriptions.add(subscription);
    }

    /**
     * Remove all Rx subscriptions.
     */
    private void unsubscribeAll() {
        if (mSubscriptions == null) {
            return;
        }

        for(Subscription s : mSubscriptions) {
            s.unsubscribe();
            s = null;
        }

        mSubscriptions.clear();
    }
}
