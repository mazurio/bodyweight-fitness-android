package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.view.ChangeRoutineView;

public class ChangeRoutinePresenter extends IPresenter<ChangeRoutineView> {
    @Override
    public void onSubscribe() {
        super.onSubscribe();
    }

    public void onClickRoutine0() {
        RoutineStream.getInstance().setRoutine(0);
    }

    public void onClickRoutine1() {
        RoutineStream.getInstance().setRoutine(1);
    }

    public void onClickRoutine2() {
        RoutineStream.getInstance().setRoutine(1);
    }
}
