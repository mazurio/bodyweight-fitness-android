package io.mazur.fit.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.UUID;

import io.mazur.fit.R;
import io.mazur.fit.model.realm.RealmExercise;
import io.mazur.fit.model.realm.RealmRoutine;
import io.mazur.fit.model.realm.RealmSet;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressPresenter> {
    private static final int MAXIMUM_NUMBER_OF_REPS = 10;

    private RealmRoutine mRealmRoutine;

    public ProgressAdapter(RealmRoutine realmRoutine) {
        super();

        mRealmRoutine = realmRoutine;
    }

    @Override
    public ProgressPresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProgressPresenter(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_progress_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ProgressPresenter holder, int position) {
        holder.bindView(mRealmRoutine.getExercises().get(position));
    }

    @Override
    public int getItemCount() {
        return mRealmRoutine.getExercises().size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class ProgressPresenter extends RecyclerView.ViewHolder {
        private CardView cardView;
        private View cardLayout;
        private TextView sectionTitle;
        private TextView exerciseTitle;
        private Toolbar toolbar;
        private LinearLayout sets;

        public ProgressPresenter(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            cardLayout = itemView.findViewById(R.id.cardLayout);
//            sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
            exerciseTitle = (TextView) itemView.findViewById(R.id.exerciseTitle);
            toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_log_workout);

            sets = (LinearLayout) itemView.findViewById(R.id.sets);
        }

        public void bindView(final RealmExercise realmExercise) {
            itemView.setOnClickListener(v -> {
                if(sets.getVisibility() == View.GONE) {
                    sets.setVisibility(View.VISIBLE);
                } else {
                    sets.setVisibility(View.GONE);
                }
            });

            exerciseTitle.setText(realmExercise.getTitle());

//            if(realmExercise.getSectionOrder() == 0) {
//                sectionTitle.setText(realmExercise.getSection());
//                sectionTitle.setVisibility(View.VISIBLE);
//            } else {
//                sectionTitle.setVisibility(View.GONE);
//            }

            toolbar.setOnMenuItemClickListener(item -> {
                int numberOfSets = 0;

                switch (item.getItemId()) {
                    case R.id.action_add_set:
                        numberOfSets = realmExercise.getSets().size();

                        // We do not want user to add more than 10 sets to one exercise.
                        if(numberOfSets < 10) {
                            RealmSet realmSet = new RealmSet();
                            realmSet.setId(UUID.randomUUID().toString());
                            realmSet.setValue(0);

                            realmExercise.getSets().add(realmSet);

                            redrawSets(realmExercise);
                        }

                        return true;

                    case R.id.action_remove_set:
                        numberOfSets = realmExercise.getSets().size();

                        // We always keep at least one set.
                        if(numberOfSets > 1) {
                            realmExercise.getSets().remove(numberOfSets - 1);

                            redrawSets(realmExercise);
                        }

                        return true;
                }

                return false;
            });

            redrawSets(realmExercise);
        }

        public void redrawSets(final RealmExercise realmExercise) {
            sets.removeAllViews();

            for(final RealmSet set : realmExercise.getSets()) {
                final View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.activity_progress_set, sets, false);
                final Button button = (Button) view.findViewById(R.id.button);

                if(set.getValue() == 0) {
                    button.setText("/");
                } else {
                    button.setText(String.valueOf(set.getValue()));
                }

                button.setOnClickListener(v -> {
                    if (set.getValue() >= MAXIMUM_NUMBER_OF_REPS) {
                        set.setValue(0);
                        button.setText("/");
                    } else {
                        set.setValue(set.getValue() + 1);
                        button.setText(String.valueOf(set.getValue()));
                    }
                });

                sets.addView(view);
            }
        }
    }
}
