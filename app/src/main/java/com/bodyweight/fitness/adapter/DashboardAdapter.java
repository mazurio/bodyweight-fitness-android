package com.bodyweight.fitness.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.LinkedRoutine;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.model.RoutineType;
import com.bodyweight.fitness.model.Section;
import com.bodyweight.fitness.model.SectionMode;
import com.bodyweight.fitness.utils.Logger;

import java.util.HashMap;
import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardAbstractPresenter> {
    public class Tuple {
        public LinkedRoutine left;
        public LinkedRoutine right;

        public Tuple(LinkedRoutine left) {
            this.left = left;
        }

        public Tuple(LinkedRoutine left, LinkedRoutine right) {
            this.left = left;
            this.right = right;
        }
    }

    public interface OnExerciseClickListener {
        void onExerciseClicked(Exercise exercise);
    }

    private Routine mRoutine;
    private HashSet<Section> mSet = new HashSet<>();
    private HashMap<Integer, Tuple> mMap = new HashMap<>();

    private OnExerciseClickListener mOnExerciseClickListener;

    public void setOnExerciseClickListener(OnExerciseClickListener onExerciseClickListener) {
        mOnExerciseClickListener = onExerciseClickListener;
    }

    public DashboardAdapter(Routine routine) {
        mRoutine = routine;

        int index = 0;
        boolean skip = false;

        for (Exercise exercise : mRoutine.getLinkedExercises()) {
            if (skip) {
                skip = false;
            } else {
                if (!mSet.contains(exercise.getSection())) {
                    Logger.d("Adding section " + index + " " + exercise.getSection().getTitle());

                    mSet.add(exercise.getSection());
                    mMap.put(index, new Tuple(exercise.getSection()));

                    index++;
                }

                Logger.d("Adding exercise " + index + " " + exercise.getTitle());

                if (exercise.getSection().getSectionMode().equals(SectionMode.ALL) && exercise.getNext() != null) {
                    if (exercise.getNext().getSection().equals(exercise.getSection())) {
                        if (index % 2 == 0) {
                            mMap.put(index, new Tuple(exercise, exercise.getNext()));
                            skip = true;
                        } else {
                            mMap.put(index, new Tuple(exercise));
                        }
                    } else {
                        mMap.put(index, new Tuple(exercise));
                    }
                } else {
                    mMap.put(index, new Tuple(exercise));
                }

                index++;
            }
        }
    }

    @Override
    public DashboardAbstractPresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new DashboardSectionPresenter(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.view_dashboard_section, parent, false)
            );
        }

        if (viewType == 2) {
            return new DashboardDoubleItemPresenter(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.view_dashboard_double_item, parent, false)
            );
        }

        return new DashboardSingleItemPresenter(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_dashboard_single_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(DashboardAbstractPresenter holder, int position) {
        holder.onBindView(mMap.get(position));
    }

    @Override
    public int getItemCount() {
        if (mRoutine == null) {
            return 0;
        }

        return mMap.size();
    }

    @Override
    public int getItemViewType(int position) {
        Tuple tuple = mMap.get(position);

        if (tuple.left.getType().equals(RoutineType.SECTION)) {
            return 1;
        }

        if (tuple.right != null) {
            return 2;
        }

        return 0;
    }

    public abstract class DashboardAbstractPresenter extends RecyclerView.ViewHolder {
        public DashboardAbstractPresenter(View itemView) {
            super(itemView);
        }

        public abstract void onBindView(Tuple tuple);
    }

    public class DashboardSectionPresenter extends DashboardAbstractPresenter {
        @InjectView(R.id.section_title)
        TextView mSectionTitle;

        public DashboardSectionPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onBindView(Tuple tuple) {
            Section section = (Section) tuple.left;

            mSectionTitle.setText(section.getTitle());
        }
    }

    public class DashboardSingleItemPresenter extends DashboardAbstractPresenter {
        @InjectView(R.id.exercise_title)
        TextView mExerciseTitle;

        Tuple mTuple;

        public DashboardSingleItemPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onBindView(Tuple tuple) {
            mTuple = tuple;

            Exercise exercise = (Exercise) tuple.left;

            mExerciseTitle.setText(exercise.getTitle());
        }

        @OnClick(R.id.circle)
        public void onCircleClick() {
            Exercise exercise = (Exercise) mTuple.left;

            mOnExerciseClickListener.onExerciseClicked(exercise);
        }
    }

    public class DashboardDoubleItemPresenter extends DashboardAbstractPresenter {
        @InjectView(R.id.left_exercise_title)
        TextView mLeftExerciseTitle;

        @InjectView(R.id.right_exercise_title)
        TextView mRightExerciseTitle;

        public DashboardDoubleItemPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onBindView(Tuple tuple) {
            Exercise leftExercise = (Exercise) tuple.left;
            Exercise rightExercise = (Exercise) tuple.right;

            mLeftExerciseTitle.setText(leftExercise.getTitle());
            mRightExerciseTitle.setText(rightExercise.getTitle());
        }
    }
}