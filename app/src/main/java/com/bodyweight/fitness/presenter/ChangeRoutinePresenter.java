package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.adapter.ChangeRoutineListAdapter;
import com.bodyweight.fitness.view.ChangeRoutineView;

public class ChangeRoutinePresenter extends IPresenter<ChangeRoutineView> {
    private transient ChangeRoutineListAdapter mChangeRoutineListAdapter;

    @Override
    public void onCreateView(ChangeRoutineView view) {
        super.onCreateView(view);

        mChangeRoutineListAdapter = new ChangeRoutineListAdapter();

        mView.setListAdapter(mChangeRoutineListAdapter);
    }
}
