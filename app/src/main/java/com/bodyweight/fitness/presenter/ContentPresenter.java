package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.stream.Stream;
import com.bodyweight.fitness.view.ContentView;

import com.bodyweight.fitness.R;

public class ContentPresenter extends IPresenter<ContentView> {
    private Integer mId = R.id.action_menu_home;

    @Override
    public void onRestoreInstanceState(ContentView view) {
        super.onRestoreInstanceState(view);

        setContent(mId);
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(Stream.INSTANCE
                .getDrawerObservable()
                .filter(id ->
                        id.equals(R.id.action_menu_home) ||
                        id.equals(R.id.action_menu_change_routine) ||
                        id.equals(R.id.action_menu_workout_log)
                )
                .subscribe(id -> {
                    mId = id;
                    setContent(id);
                }));

        subscribe(RoutineStream.getInstance()
                .getRoutineChangedObservable()
                .subscribe(routine -> {
                    mView.showHome();
                }));
    }

    public void setContent(Integer id) {
        if (id.equals(R.id.action_menu_home)) {
            mView.showHome();
        } else if (id.equals(R.id.action_menu_change_routine)) {
            mView.showChangeRoutine();
        } else if (id.equals(R.id.action_menu_workout_log)) {
            mView.showCalendar();
        }
    }
}
