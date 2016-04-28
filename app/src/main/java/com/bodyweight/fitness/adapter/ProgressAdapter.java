package com.bodyweight.fitness.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bodyweight.fitness.model.repository.RepositoryCategory;
import com.bodyweight.fitness.model.repository.RepositoryExercise;
import com.bodyweight.fitness.model.repository.RepositorySection;
import com.bodyweight.fitness.model.repository.RepositorySet;
import com.bodyweight.fitness.stream.DialogType;
import com.bodyweight.fitness.stream.UiEvent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.bodyweight.fitness.R;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressPresenter> {
    private RepositoryCategory mRepositoryCategory;

    private HashMap<Integer, RepositorySection> mItemViewMapping = new HashMap<>();
    private HashMap<Integer, RepositoryExercise> mExerciseViewMapping = new HashMap<>();

    private int mTotalSize = 0;

    public ProgressAdapter(RepositoryCategory repositoryCategory) {
        super();

        mRepositoryCategory = repositoryCategory;

        /**
         * We loop over the sections in order to find out the item view id
         * for each section in the Recycler View.
         */
        int sectionId = 0;
        int exerciseId = 1;
        for(RepositorySection repositorySection : mRepositoryCategory.getSections()) {
            mItemViewMapping.put(sectionId, repositorySection);

            int numberOfExercises = 0;

            for(RepositoryExercise repositoryExercise : repositorySection.getExercises()) {
                if(repositoryExercise.isVisible()) {
                    mExerciseViewMapping.put(exerciseId, repositoryExercise);

                    exerciseId += 1;
                    mTotalSize += 1;

                    numberOfExercises++;
                }
            }

            sectionId = sectionId + numberOfExercises + 1;
            exerciseId += 1;
            mTotalSize += 1;
        }
    }

    @Override
    public ProgressPresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            return new ProgressTitlePresenter(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.activity_progress_title, parent, false));
        }

        return new ProgressCardPresenter(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_progress_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ProgressPresenter holder, int position) {
        if (mItemViewMapping.containsKey(position)) {
            ProgressTitlePresenter presenter = (ProgressTitlePresenter) holder;
            presenter.bindView(mItemViewMapping.get(position));
        } else if (mExerciseViewMapping.containsKey(position)) {
            ProgressCardPresenter presenter = (ProgressCardPresenter) holder;
            presenter.bindView(mExerciseViewMapping.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mTotalSize;
    }

    @Override
    public int getItemViewType(int position) {
        if(mItemViewMapping.containsKey(position)) {
            return 1;
        }

        return 0;
    }

    public abstract class ProgressPresenter extends RecyclerView.ViewHolder {
        public ProgressPresenter(View itemView) {
            super(itemView);
        }
    }

    public class ProgressCardPresenter extends ProgressPresenter {
        @InjectView(R.id.toolbar) Toolbar mToolbar;
        @InjectView(R.id.view_button) Button mButton;

        private RepositoryExercise mRepositoryExercise;

        public ProgressCardPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void bindView(RepositoryExercise repositoryExercise) {
            mRepositoryExercise = repositoryExercise;

            mToolbar.setTitle(repositoryExercise.getTitle());
            mToolbar.setSubtitle(repositoryExercise.getDescription());

            if(!isCompleted(mRepositoryExercise)) {
                mToolbar.setSubtitle("Not completed");
            }

            mButton.setOnClickListener(view -> {
                UiEvent.INSTANCE.showDialog(DialogType.LogWorkout, mRepositoryExercise.getExerciseId());
            });
        }
    }

    public class ProgressTitlePresenter extends ProgressPresenter {
        @InjectView(R.id.title) TextView mTitle;

        public ProgressTitlePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void bindView(RepositorySection repositorySection) {
            mTitle.getPaddingTop();

            if (getLayoutPosition() == 0) {
                mTitle.setPadding(
                        mTitle.getPaddingLeft(),
                        mTitle.getPaddingLeft(),
                        mTitle.getPaddingRight(),
                        mTitle.getPaddingBottom()
                );
            } else {
                mTitle.setPadding(
                        mTitle.getPaddingLeft(),
                        mTitle.getPaddingBottom(),
                        mTitle.getPaddingRight(),
                        mTitle.getPaddingBottom()
                );
            }

            mTitle.setText(repositorySection.getTitle());
        }
    }

    public boolean isCompleted(RepositoryExercise repositoryExercise) {
        int size = repositoryExercise.getSets().size();

        if (size == 0) {
            return false;
        }

        RepositorySet firstSet = repositoryExercise.getSets().get(0);

        if(size == 1 && firstSet.getSeconds() == 0 && firstSet.getReps() == 0) {
            return false;
        }

        return true;
    }
}
