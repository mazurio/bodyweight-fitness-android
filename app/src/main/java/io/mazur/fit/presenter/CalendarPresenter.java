package io.mazur.fit.presenter;

import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.Locale;

import io.mazur.fit.R;
import io.mazur.fit.adapter.CalendarAdapter;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.stream.CalendarStream;
import io.mazur.fit.stream.RealmStream;
import io.mazur.fit.stream.ToolbarStream;
import io.mazur.fit.ui.ProgressActivity;
import io.mazur.fit.utils.DateUtils;
import io.mazur.fit.view.CalendarView;

import io.realm.Realm;

public class CalendarPresenter {
    private transient CalendarView mCalendarView;

    private CalendarAdapter mCalendarAdapter;

    private String mRealmRoutineId;

    public void onCreateView(CalendarView calendarView) {
        mCalendarView = calendarView;

        mCalendarAdapter = new CalendarAdapter(
                mCalendarView.getViewPager()
        );

        mCalendarView.getViewPager().setAdapter(mCalendarAdapter);
        mCalendarView.getViewPager().setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false);

        RealmStream.getInstance()
                .getRealmRoutineObservable()
                .subscribe(realmRoutine -> {
                    notifyDataSetChanged();
                });

        CalendarStream.getInstance()
                .getCalendarDayChangedObservable()
                .subscribe(calendarDayChanged -> {
                    DateTime dateTime = DateUtils.getDate(
                            calendarDayChanged.presenterSelected,
                            calendarDayChanged.daySelected
                    );

                    mCalendarView.getDate().setText(dateTime.toString("EEEE, d MMMM", Locale.ENGLISH));

                    if (isRoutineLogged(dateTime)) {
                        showCardView();
                    } else {
                        mCalendarView.hideCardView();
                    }
                });

        ToolbarStream.getInstance()
                .getMenuObservable()
                .filter(id -> id.equals(R.id.action_today))
                .subscribe(id -> {
                    mCalendarView.getViewPager().setCurrentItem(
                            CalendarAdapter.DEFAULT_POSITION, true
                    );
                });
    }

    private void showCardView() {
        mCalendarView.showCardView();

        mCalendarView.getViewButton().setOnClickListener(view -> {
            Intent intent = new Intent(mCalendarView.getContext(), ProgressActivity.class);

            intent.putExtra("routineId", mRealmRoutineId);

            mCalendarView.getContext().startActivity(intent);
        });

        mCalendarView.getExportButton().setOnClickListener(view -> {
            Realm realm = RealmStream.getInstance().getRealm();

            RealmRoutine realmRoutine = realm.where(RealmRoutine.class)
                    .equalTo("id", mRealmRoutineId)
                    .findFirst();

            String s = "Category Title,Section Title,Exercise Title,Exercise Description";
            for(RealmExercise realmExercise : realmRoutine.getExercises()) {
                s += realmExercise.getCategory().getTitle() + ",";
                s += realmExercise.getSection().getTitle() + ",";
                s += realmExercise.getTitle() + ",";
                s += realmExercise.getDescription() + "\n";
            }

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);

            mCalendarView.getContext().startActivity(Intent.createChooser(sharingIntent, "Export as CSV"));
        });

        mCalendarView.getRemoveButton().setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCalendarView.getContext())
                    .setTitle("Remove Logged Workout?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        Realm realm = RealmStream.getInstance().getRealm();

                        RealmRoutine realmRoutine = realm.where(RealmRoutine.class)
                                .equalTo("id", mRealmRoutineId)
                                .findFirst();

                        realm.beginTransaction();
                        realmRoutine.removeFromRealm();
                        realm.commitTransaction();

                        notifyDataSetChanged();
                        mCalendarView.hideCardView();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {});

            alertDialog.show();
        });
    }

    private void notifyDataSetChanged() {
        mCalendarView.getViewPager().setAdapter(mCalendarAdapter);
        mCalendarView.getViewPager().setCurrentItem(CalendarAdapter.DEFAULT_POSITION, false);
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

        Realm realm = RealmStream.getInstance().getRealm();
        RealmRoutine realmRoutine = realm.where(RealmRoutine.class)
                .between("startTime", start, end)
                .findFirst();

        if(realmRoutine != null) {
            mRealmRoutineId = realmRoutine.getId();

            return true;
        }

        return false;
    }
}
