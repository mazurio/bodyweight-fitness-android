package io.mazur.fit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import io.mazur.fit.R;
import io.mazur.fit.model.Category;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.Exercise;
import io.mazur.fit.model.LinkedRoutine;
import io.mazur.fit.model.RoutineType;
import io.mazur.fit.model.Section;
import io.mazur.fit.stream.RoutineStream;

public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.RoutinePresenter> {
    private Routine mRoutine;

    private int mActiveExerciseIndex = 1;

    @Override
    public RoutinePresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(RoutineType.fromInt(viewType)) {
            case CATEGORY: return new RoutineCategoryPresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_drawer_list_category, parent, false)
            );

            case SECTION: return new RoutineSectionPresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_drawer_list_section, parent, false)
            );

            case EXERCISE_ACTIVE: return new RoutineExercisePresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_drawer_list_exercise_active, parent, false)
            );

            default: return new RoutineExercisePresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_drawer_list_exercise, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(RoutinePresenter holder, int position) {
        holder.onBindView(mRoutine.getLinkedRoutine().get(position));
    }

    @Override
    public int getItemCount() {
        if(mRoutine == null) {
            return 0;
        }

        return mRoutine.getLinkedRoutine().size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mActiveExerciseIndex == position) {
            return RoutineType.EXERCISE_ACTIVE.ordinal();
        }

        return mRoutine.getLinkedRoutine().get(position).getType().ordinal();
    }

    public void setRoutine(Routine routine) {
        mRoutine = routine;

        notifyDataSetChanged();
    }

    public void setActiveExerciseIndex(int index) {
        mActiveExerciseIndex = index;

        notifyItemRangeChanged(0, mActiveExerciseIndex);
    }

    public abstract class RoutinePresenter<T extends LinkedRoutine> extends RecyclerView.ViewHolder {
        public RoutinePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public abstract void onBindView(T linkedRoutine);
    }

    public class RoutineCategoryPresenter extends RoutinePresenter<Category> {
        @InjectView(R.id.routine_category_text_view)
        TextView mRoutineCategoryTextView;

        public RoutineCategoryPresenter(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(Category category) {
            mRoutineCategoryTextView.setText(category.getTitle());
        }
    }

    public class RoutineSectionPresenter extends RoutinePresenter<Section> {
        @InjectView(R.id.routine_section_text_view)
        TextView mRoutineSectionTextView;

        public RoutineSectionPresenter(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(Section section) {
            mRoutineSectionTextView.setText(section.getTitle());
        }
    }

    public class RoutineExercisePresenter extends RoutinePresenter<Exercise> {
        @InjectView(R.id.routine_exercise_level_text_view)
        TextView mRoutineExerciseLevelTextView;

        @InjectView(R.id.routine_exercise_text_view)
        TextView mRoutineExerciseTextView;

        public RoutineExercisePresenter(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(Exercise exercise) {
            if(exercise.getLevel() == null) {
                mRoutineExerciseLevelTextView.setVisibility(View.GONE);
            } else {
                mRoutineExerciseLevelTextView.setVisibility(View.VISIBLE);
                mRoutineExerciseLevelTextView.setText(exercise.getLevel());
            }

            mRoutineExerciseTextView.setText(exercise.getTitle());

            itemView.setOnClickListener(new OnItemClickListener(exercise));
        }
    }

    public class OnItemClickListener implements View.OnClickListener {
        private LinkedRoutine mLinkedRoutine;

        public OnItemClickListener(LinkedRoutine linkedRoutine) {
            mLinkedRoutine = linkedRoutine;
        }

        @Override
        public void onClick(View v) {
            RoutineStream.getInstance().setExercise((Exercise) mLinkedRoutine);
        }
    }
}
