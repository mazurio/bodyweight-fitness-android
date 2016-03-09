package com.bodyweight.fitness.adapter;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;
import com.bodyweight.fitness.stream.RepositoryStream;
import com.bodyweight.fitness.ui.ProgressActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.CalendarRoutinePresenter> {
    private RealmResults<RepositoryRoutine> mResults;

    public CalendarListAdapter() {
        super();
    }

    public void setItems(RealmResults<RepositoryRoutine> results) {
        mResults = results;

        notifyDataSetChanged();
    }

    @Override
    public CalendarRoutinePresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarRoutinePresenter(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_calendar_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(CalendarRoutinePresenter holder, int position) {
        holder.onBindView(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
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
            intent.putExtra("primaryKeyRoutineId", mRepositoryRoutine.getId());

            view.getContext().startActivity(intent);
        }

        @OnClick(R.id.view_calendar_card_remove_button)
        @SuppressWarnings("unused")
        public void onClickRemoveButton(View view) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Remove Logged Workout?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        Realm realm = RepositoryStream.getInstance().getRealm();

                        RepositoryRoutine repositoryRoutine = realm.where(RepositoryRoutine.class)
                                .equalTo("id", mRepositoryRoutine.getId())
                                .findFirst();

                        realm.beginTransaction();
                        repositoryRoutine.removeFromRealm();
                        realm.commitTransaction();

                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {});

            alertDialog.show();
        }
    }
}
