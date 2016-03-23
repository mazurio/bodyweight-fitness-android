package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.model.Exercise;

import org.joda.time.DateTime;

import java.util.Locale;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.CalendarDayChanged;
import com.bodyweight.fitness.stream.CalendarStream;
import com.bodyweight.fitness.stream.DrawerStream;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.DateUtils;
import com.bodyweight.fitness.view.ToolbarView;

public class ToolbarPresenter extends IPresenter<ToolbarView> {
    private Integer mId = R.id.action_menu_home;

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(RoutineStream.getInstance()
                .getExerciseObservable()
                .filter(exercise -> mId.equals(R.id.action_menu_home))
                .subscribe(this::setToolbarForHome));

        subscribe(CalendarStream.getInstance()
                .getCalendarDayChangedObservable()
                .filter(calendarDayChanged -> mId.equals(R.id.action_menu_workout_log))
                .subscribe(this::setToolbarForCalendar));

        subscribe(DrawerStream.getInstance()
                .getMenuObservable()
                .filter(id ->
                        id.equals(R.id.action_menu_home) ||
                        id.equals(R.id.action_menu_change_routine) ||
                        id.equals(R.id.action_menu_workout_log)
                )
                .subscribe(id -> {
                    mId = id;

                    setToolbarContent(mId);
                }));
    }

    private void setToolbarContent(Integer id) {
        if (id.equals(R.id.action_menu_home)) {
            Exercise exercise = RoutineStream.getInstance().getExercise();

            setToolbarForHome(exercise);
        } else if (id.equals(R.id.action_menu_change_routine)) {
            setToolbarForChangeRoutine();
        } else if (id.equals(R.id.action_menu_workout_log)) {
            CalendarDayChanged calendarDayChanged = CalendarStream.getInstance().getCalendarDayChanged();

            setToolbarForCalendar(calendarDayChanged);
        }
    }

    private void setToolbarForHome(Exercise exercise) {
        if (exercise == null) {
            exercise = RoutineStream.getInstance().getExercise();
        }

        mView.inflateHomeMenu();

        mView.setTitle(exercise.getTitle());
        mView.setSubtitle(exercise.getSection().getTitle());
        mView.setDescription(exercise.getDescription());
    }

    private void setToolbarForChangeRoutine() {
        mView.invalidateOptionsMenu();

        mView.setSingleTitle("Change Routine");
    }

    private void setToolbarForCalendar(CalendarDayChanged calendarDayChanged) {
        DateTime dateTime;

        if (calendarDayChanged == null) {
            dateTime = new DateTime();
        } else {
            calendarDayChanged = CalendarStream.getInstance().getCalendarDayChanged();

            if (calendarDayChanged == null) {
                dateTime = new DateTime();
            } else {
                dateTime = DateUtils.getDate(
                        calendarDayChanged.presenterSelected,
                        calendarDayChanged.daySelected
                );
            }
        }

        mView.inflateCalendarMenu();

        mView.setSingleTitle(dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH));
    }
}
