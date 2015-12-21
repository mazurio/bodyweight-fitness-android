package io.mazur.fit.presenter;

import io.mazur.fit.adapter.DrawerListAdapter;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.DrawerListView;

import rx.Observable;
import rx.Subscription;

public class DrawerListPresenter extends IPresenter <DrawerListView> {
    private transient Subscription mSubscription;

    private DrawerListAdapter mDrawerListAdapter;

    public DrawerListPresenter() {
        mDrawerListAdapter = new DrawerListAdapter();
    }

    @Override
    public void onCreateView(DrawerListView view) {
        super.onCreateView(view);

        mView.getRecyclerView().setAdapter(mDrawerListAdapter);

        mSubscription = Observable.merge(
                RoutineStream.getInstance().getRoutineObservable(),
                RoutineStream.getInstance().getLevelChangedObservable()
        ).subscribe(mDrawerListAdapter::setRoutine);
    }

    @Override
    public void onSaveInstanceState() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    public void onRestoreInstanceState() {

    }
}
