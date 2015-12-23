package io.mazur.fit.presenter;

import android.support.v7.widget.LinearLayoutManager;

import io.mazur.fit.adapter.DrawerListAdapter;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.DrawerListView;

import rx.Observable;

public class DrawerListPresenter extends IPresenter <DrawerListView> {
    private transient DrawerListAdapter mDrawerListAdapter;

    private int mIndex = 1;

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(Observable.merge(
                RoutineStream.getInstance().getRoutineObservable(),
                RoutineStream.getInstance().getLevelChangedObservable()
        ).subscribe(routine -> {
            // TODO: This is a work around stupid fragment creation.
            if (mDrawerListAdapter != null) {
                mDrawerListAdapter.setRoutine(routine);
            }
        }));

        subscribe(RoutineStream.getInstance()
                .getExerciseChangedObservable()
                .subscribe(exercise -> {
                    mIndex = RoutineStream.getInstance()
                            .getRoutine()
                            .getLinkedRoutine()
                            .indexOf(exercise);

                    if (mDrawerListAdapter != null) {
                        mDrawerListAdapter.setActiveExerciseIndex(mIndex);
                    }
                }));
    }

    @Override
    public void onCreateView(DrawerListView view) {
        super.onCreateView(view);

        mDrawerListAdapter = new DrawerListAdapter();
        mDrawerListAdapter.setActiveExerciseIndex(mIndex);

        mView.getRecyclerView().setLayoutManager(
                new LinearLayoutManager(mView.getContext())
        );

        mView.getRecyclerView().setAdapter(mDrawerListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mDrawerListAdapter = null;
    }

    @Override
    public void onSaveInstanceState() {
        super.onSaveInstanceState();

        mDrawerListAdapter = null;
    }

    @Override
    public void onRestoreInstanceState(DrawerListView view) {
        super.onRestoreInstanceState(view);

        mDrawerListAdapter = new DrawerListAdapter();
        mDrawerListAdapter.setActiveExerciseIndex(mIndex);

        mView.getRecyclerView().setLayoutManager(
                new LinearLayoutManager(mView.getContext())
        );

        mView.getRecyclerView().setAdapter(mDrawerListAdapter);
    }
}
