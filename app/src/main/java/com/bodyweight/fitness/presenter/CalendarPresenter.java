package com.bodyweight.fitness.presenter;

import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.utils.DateUtils;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Locale;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.adapter.CalendarAdapter;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.stream.CalendarStream;
import com.bodyweight.fitness.stream.ToolbarStream;
import com.bodyweight.fitness.ui.ProgressActivity;
import com.bodyweight.fitness.view.CalendarView;

import io.realm.Realm;

public class CalendarPresenter extends IPresenter<CalendarView> {
    private transient CalendarAdapter mCalendarAdapter;
    private String mRealmRoutineId;

    @Override
    public void onCreateView(CalendarView view) {
        super.onCreateView(view);

        mCalendarAdapter = new CalendarAdapter();

        mView.setAdapter(mCalendarAdapter);
        mView.scrollToDefaultItem();
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(RepositoryStream.getInstance()
                .getRepositoryRoutineObservable()
                .subscribe(realmRoutine -> {
                    mView.setAdapter(mCalendarAdapter);
                    mView.scrollToDefaultItem();
                }));

        subscribe(ToolbarStream.getInstance()
                .getMenuObservable()
                .filter(id -> id.equals(R.id.action_today))
                .subscribe(id -> {
                    mView.scrollToDefaultItem();
                }));

        subscribe(CalendarStream.getInstance()
                .getCalendarDayChangedObservable()
                .subscribe(calendarDayChanged -> {
                    DateTime dateTime = DateUtils.getDate(
                            calendarDayChanged.presenterSelected,
                            calendarDayChanged.daySelected
                    );

                    mView.setDate(
                            dateTime.toString("EEEE, d MMMM", Locale.ENGLISH)
                    );

                    if (isRoutineLogged(dateTime)) {
                        mView.showCardView();
                    } else {
                        mView.hideCardView();
                    }
                }));
    }

    public boolean isRoutineLogged(DateTime dateTime) {
        final Date start = dateTime
                .withTimeAtStartOfDay()
                .toDate();

        final Date end = dateTime
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toDate();

        Realm realm = RepositoryStream.getInstance().getRealm();
        RepositoryRoutine repositoryRoutine = realm.where(RepositoryRoutine.class)
                .between("startTime", start, end)
                .findFirst();

        if(repositoryRoutine != null) {
            mRealmRoutineId = repositoryRoutine.getId();

            return true;
        }

        return false;
    }

    public void onPageSelected(int position) {
        CalendarStream.getInstance().setCalendarPage(position);
    }

    public void onClickViewButton() {
        Intent intent = new Intent(getContext(), ProgressActivity.class);
        intent.putExtra("routineId", mRealmRoutineId);

        getContext().startActivity(intent);
    }

    public void onClickExportButton() {
        Realm realm = RepositoryStream.getInstance().getRealm();

        RepositoryRoutine repositoryRoutine = realm.where(RepositoryRoutine.class)
                .equalTo("id", mRealmRoutineId)
                .findFirst();

        String s = "Category Title,Section Title,Exercise Title,Exercise Description";
        for(RepositoryExercise repositoryExercise : repositoryRoutine.getExercises()) {
            s += repositoryExercise.getCategory().getTitle() + ",";
            s += repositoryExercise.getSection().getTitle() + ",";
            s += repositoryExercise.getTitle() + ",";
            s += repositoryExercise.getDescription() + "\n";
        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);

        getContext().startActivity(Intent.createChooser(sharingIntent, "Export as CSV"));
    }

    public void onClickRemoveButton() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Remove Logged Workout?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    Realm realm = RepositoryStream.getInstance().getRealm();

                    RepositoryRoutine repositoryRoutine = realm.where(RepositoryRoutine.class)
                            .equalTo("id", mRealmRoutineId)
                            .findFirst();

                    realm.beginTransaction();
                    repositoryRoutine.removeFromRealm();
                    realm.commitTransaction();

                    mView.setAdapter(mCalendarAdapter);
                    mView.scrollToDefaultItem();

                    mView.hideCardView();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        alertDialog.show();
    }
}
