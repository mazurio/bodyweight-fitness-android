package com.bodyweight.fitness.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.ui.ProgressActivity;
import com.bodyweight.fitness.utils.Logger;
import com.bodyweight.fitness.utils.PreferenceUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import io.realm.Realm;
import io.realm.RealmResults;

public class CalendarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RealmResults<RepositoryRoutine> mResults;

    public CalendarListAdapter() {
        super();
    }

    public void setItems(RealmResults<RepositoryRoutine> results) {
        mResults = results;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == 1) {
//            return new CalendarSummaryPresenter(
//                    LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.view_calendar_summary, parent, false)
//            );
//        }

        return new CalendarRoutinePresenter(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_calendar_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position == 0) {
//            return;
//        }

//        ((CalendarRoutinePresenter) holder).onBindView(mResults.get(position - 1));
        ((CalendarRoutinePresenter) holder).onBindView(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        if (mResults != null) {
            return mResults.size();
        }

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return 1;
//        }

        return 0;
    }

    public class CalendarSummaryPresenter extends RecyclerView.ViewHolder {
        public CalendarSummaryPresenter(View itemView) {
            super(itemView);
        }
    }

    public class CalendarRoutinePresenter extends RecyclerView.ViewHolder {
        @InjectView(R.id.view_calendar_routine_title)
        TextView mTitle;

        @InjectView(R.id.view_calendar_routine_subtitle)
        TextView mSubtitle;

        private RepositoryRoutine mRepositoryRoutine;

        public CalendarRoutinePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void onBindView(RepositoryRoutine repositoryRoutine) {
            mRepositoryRoutine = repositoryRoutine;

            mTitle.setText(repositoryRoutine.getTitle());
            mSubtitle.setText(repositoryRoutine.getSubtitle());
        }

        @OnClick(R.id.view_calendar_card_view_button)
        @SuppressWarnings("unused")
        public void onClickViewButton(View view) {
            Intent intent = new Intent(view.getContext(), ProgressActivity.class);
            intent.putExtra(Constants.INSTANCE.getPRIMARY_KEY_ROUTINE_ID(), mRepositoryRoutine.getId());

            view.getContext().startActivity(intent);
        }

        @OnClick(R.id.view_calendar_card_export_button)
        @SuppressWarnings("unused")
        public void onClickExportButton(View view) {

            exportHTML();
            exportCSV();

//            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//
//            emailIntent.setType("*/*");
//            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {
//                    "me@gmail.com"
//            });
//
//            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
//            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "go on read the emails");
//
//            itemView.getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }

        @OnClick(R.id.view_calendar_card_remove_button)
        @SuppressWarnings("unused")
        public void onClickRemoveButton(View view) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Remove Logged Workout?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Realm realm = RepositoryStream.getInstance().getRealm();

                            RepositoryRoutine repositoryRoutine = realm.where(RepositoryRoutine.class)
                                    .equalTo("id", mRepositoryRoutine.getId())
                                    .findFirst();

                            realm.beginTransaction();
                            repositoryRoutine.removeFromRealm();
                            realm.commitTransaction();

                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            alertDialog.show();
        }

        private void exportHTML() {
            Logger.d("Hello, The following is your workout in Text/HTML format (CSV attached).");
            Logger.d(String.format("Workout on %s.", new DateTime(mRepositoryRoutine.getStartTime()).toString("EEEE, d MMMM YYYY, HH:mm")));
            Logger.d(String.format("Finished at %s.", new DateTime(mRepositoryRoutine.getLastUpdatedTime()).toString("HH:mm")));
            Logger.d(String.format("Workout length: %s.", getWorkoutLength()));
            Logger.d(String.format("\n%s - %s\n", mRepositoryRoutine.getTitle(), mRepositoryRoutine.getSubtitle()));
        }

        private void exportCSV() {
            String header = "Date, Start Time, End Time, Workout Length, Routine, Exercise, Set Order, Weight, Weight Unit, Reps, Minutes, Seconds";

            Logger.d(header);

            String routineTitle = mRepositoryRoutine.getTitle() + " - " + mRepositoryRoutine.getSubtitle();
            int index = 1;
            for (RepositoryExercise exercise : mRepositoryRoutine.getExercises()) {
                String title = exercise.getTitle();
                String weightUnit = PreferenceUtils.getInstance().getWeightMeasurementUnit().toString();

                for (RepositorySet set : exercise.getSets()) {
                    Logger.d(String.format(
                            "%s,%s,%s,%s,%s,%s,%d,%.2f,%s,%d,%s,%s\n",
                            new DateTime(mRepositoryRoutine.getStartTime()).toString("EEEE d MMMM", Locale.ENGLISH),
                            new DateTime(mRepositoryRoutine.getStartTime()).toString("HH:mm", Locale.ENGLISH),
                            new DateTime(mRepositoryRoutine.getLastUpdatedTime()).toString("HH:mm", Locale.ENGLISH),
                            getWorkoutLength(),
                            routineTitle,
                            title,
                            index,
                            set.getWeight(),
                            weightUnit,
                            set.getReps(),
                            formatMinutes(set.getSeconds()),
                            formatSeconds(set.getSeconds())
                    ));
                }

                index += 1;
            }
        }

        public String getWorkoutLength() {
            DateTime startTime = new DateTime(mRepositoryRoutine.getStartTime());
            DateTime lastUpdatedTime = new DateTime(mRepositoryRoutine.getLastUpdatedTime());

            Duration duration = new Duration(startTime, lastUpdatedTime);

            if (duration.toStandardMinutes().getMinutes() < 10) {
                return "--";
            } else {
                String formatted = new PeriodFormatterBuilder()
                        .appendHours()
                        .appendSuffix("h ")
                        .appendMinutes()
                        .appendSuffix("m")
                        .toFormatter()
                        .print(duration.toPeriod());

                return formatted;
            }
        }

        public String formatMinutes(int seconds) {
            int minutes = seconds / 60;

            if (minutes == 0) {
                return "0";
            } else if (minutes < 10) {
                return "" + minutes;
            }

            return String.valueOf(minutes);
        }

        public String formatSeconds(int seconds) {
            int s = seconds % 60;

            if (s == 0) {
                return "0";
            } else if (s < 10) {
                return "0" + s;
            }

            return String.valueOf(s);
        }
    }
}
