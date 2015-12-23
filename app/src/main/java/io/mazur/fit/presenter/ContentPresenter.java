package io.mazur.fit.presenter;

import io.mazur.fit.R;
import io.mazur.fit.stream.DrawerStream;
import io.mazur.fit.view.ContentView;

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

        subscribe(DrawerStream.getInstance()
                .getMenuObservable()
                .filter(id -> id.equals(R.id.action_menu_home) || id.equals(R.id.action_menu_workout_log))
                .subscribe(id -> {
                    mId = id;
                    setContent(id);
                }));
    }

    public void setContent(Integer id) {
        if (id.equals(R.id.action_menu_home)) {
            mView.showHome();
        } else if (id.equals(R.id.action_menu_workout_log)) {
            mView.showCalendar();
        }
    }
}
