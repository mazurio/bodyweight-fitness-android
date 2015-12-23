package io.mazur.fit.presenter;

import io.mazur.fit.adapter.DrawerListAdapter;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.DrawerListView;

import rx.Observable;

public class DrawerListPresenter extends IPresenter<DrawerListView> {
    private DrawerListAdapter mDrawerListAdapter;

    private int mIndex = 1;

    /**
     * Drawer fragment calls onCreateView twice, good luck figuring out why.
     * Stupid fragments.
     */
    private boolean onCreated = false;
    private boolean onSubscribed = false;

    @Override
    public void onCreateView(DrawerListView view) {
        if (onCreated) {
            return;
        }

        onCreated = true;

        super.onCreateView(view);

        mDrawerListAdapter = new DrawerListAdapter();
        mDrawerListAdapter.setActiveExerciseIndex(mIndex);

        mView.setAdapter(mDrawerListAdapter);
    }

    @Override
    public void onSubscribe() {
        if (onSubscribed) {
            return;
        }

        onSubscribed = true;

        super.onSubscribe();

        subscribe(Observable.merge(
                RoutineStream.getInstance().getRoutineObservable(),
                RoutineStream.getInstance().getLevelChangedObservable()
        ).subscribe(routine -> {
            mDrawerListAdapter.setRoutine(routine);
        }));

        subscribe(Observable.merge(
                RoutineStream.getInstance().getExerciseObservable(),
                RoutineStream.getInstance().getExerciseChangedObservable()
        ).subscribe(exercise -> {
            mIndex = RoutineStream.getInstance()
                    .getRoutine()
                    .getLinkedRoutine()
                    .indexOf(exercise);

            mDrawerListAdapter.setActiveExerciseIndex(mIndex);
        }));
    }
}
