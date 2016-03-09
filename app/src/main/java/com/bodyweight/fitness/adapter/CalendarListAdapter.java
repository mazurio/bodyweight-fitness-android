package com.bodyweight.fitness.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.repository.RepositoryRoutine;

import butterknife.ButterKnife;
import butterknife.InjectView;

import butterknife.OnClick;
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

        public CalendarRoutinePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void onBindView(RepositoryRoutine repositoryRoutine) {
            mTitle.setText(repositoryRoutine.getTitle());
            mSubtitle.setText(repositoryRoutine.getSubtitle());
        }

        @OnClick(R.id.view_calendar_card_view_button)
        public void onClickViewButton(View view) {
            
        }

        @OnClick(R.id.view_calendar_card_remove_button)
        public void onClickRemoveButton(View view) {

        }
    }
}
