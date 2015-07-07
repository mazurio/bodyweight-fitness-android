package io.mazur.fit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.mazur.fit.R;
import io.mazur.fit.model.Routine;
import io.mazur.fit.model.RoutineType;
import io.mazur.fit.stream.RoutineStream;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutinePresenter> {
    private Routine mRoutine;

    @Override
    public RoutinePresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(RoutineType.fromInt(viewType)) {
            case CATEGORY: return new RoutineCategoryPresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_routine_category, parent, false)
            );

            case SECTION: return new RoutineSectionPresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_routine_section, parent, false)
            );

            default: return new RoutineExercisePresenter(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.view_routine_exercise, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(RoutinePresenter holder, int position) {
        holder.onBindView(mRoutine.getPartRoutines().get(position));
    }

    @Override
    public int getItemCount() {
        if(mRoutine == null) {
            return 0;
        }

        return mRoutine.getSize();
    }

    @Override
    public int getItemViewType(int position) {
        return mRoutine.getPartRoutines().get(position).getType().ordinal();
    }

    public void setRoutine(Routine routine) {
        mRoutine = routine;

        notifyDataSetChanged();
    }

    public abstract class RoutinePresenter extends RecyclerView.ViewHolder {
        public RoutinePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public abstract void onBindView(Routine.PartRoutine partRoutine);
    }

    public class RoutineCategoryPresenter extends RoutinePresenter {
        @InjectView(R.id.routine_category_text_view) TextView mRoutineCategoryTextView;

        public RoutineCategoryPresenter(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(Routine.PartRoutine partRoutine) {
            mRoutineCategoryTextView.setText(partRoutine.getTitle());
        }
    }

    public class RoutineSectionPresenter extends RoutinePresenter {
        @InjectView(R.id.routine_section_text_view) TextView mRoutineSectionTextView;

        public RoutineSectionPresenter(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(Routine.PartRoutine partRoutine) {
            mRoutineSectionTextView.setText(partRoutine.getTitle());
        }
    }

    public class RoutineExercisePresenter extends RoutinePresenter {
        @InjectView(R.id.routine_exercise_text_view) TextView mRoutineExerciseTextView;

        public RoutineExercisePresenter(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(Routine.PartRoutine partRoutine) {
            mRoutineExerciseTextView.setText(partRoutine.getTitle());

            itemView.setOnClickListener(new OnItemClickListener(partRoutine));
        }
    }

    public class OnItemClickListener implements View.OnClickListener {
        private Routine.PartRoutine mPartRoutine;

        public OnItemClickListener(Routine.PartRoutine partRoutine) {
            mPartRoutine = partRoutine;
        }

        @Override
        public void onClick(View v) {
            RoutineStream.getInstance().setExercise(mPartRoutine);
        }
    }
}
