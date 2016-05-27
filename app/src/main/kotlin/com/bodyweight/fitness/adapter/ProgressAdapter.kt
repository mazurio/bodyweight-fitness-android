package com.bodyweight.fitness.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bodyweight.fitness.*

import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositorySection
import com.bodyweight.fitness.stream.DialogType
import com.bodyweight.fitness.stream.UiEvent

import java.util.HashMap

import com.bodyweight.fitness.dialog.LogWorkoutPresenter
import com.bodyweight.fitness.model.*

import kotlinx.android.synthetic.main.activity_progress_card.view.*
import kotlinx.android.synthetic.main.activity_progress_card_set.view.*
import kotlinx.android.synthetic.main.activity_progress_header.view.*
import kotlinx.android.synthetic.main.activity_progress_title.view.*

enum class ProgressAdapterViewType {
    Header,
    Section,
    Exercise
}

class ProgressAdapter(private val repositoryCategory: RepositoryCategory) : RecyclerView.Adapter<ProgressPresenter>() {
    private val indexViewTypeHashMap = HashMap<Int, Int>()
    private val indexSectionHashMap = HashMap<Int, RepositorySection>()
    private val indexExerciseHashMap = HashMap<Int, RepositoryExercise>()

    init {
        var index = 0

        indexViewTypeHashMap.put(index, ProgressAdapterViewType.Header.ordinal)

        index += 1

        for (repositorySection in repositoryCategory.sections) {
            indexViewTypeHashMap.put(index, ProgressAdapterViewType.Section.ordinal)
            indexSectionHashMap.put(index, repositorySection)

            index += 1

            for (repositoryExercise in RepositoryRoutine.getVisibleAndCompletedExercises(repositorySection.exercises)) {
                indexViewTypeHashMap.put(index, ProgressAdapterViewType.Exercise.ordinal)
                indexExerciseHashMap.put(index, repositoryExercise)

                index += 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressPresenter {
        when (viewType) {
            ProgressAdapterViewType.Header.ordinal -> {
                val view = parent.inflate(R.layout.activity_progress_header)

                return ProgressHeaderPresenter(view)
            }

            ProgressAdapterViewType.Section.ordinal -> {
                val view = parent.inflate(R.layout.activity_progress_title)

                return ProgressTitlePresenter(view)
            }

            else -> {
                val view = parent.inflate(R.layout.activity_progress_card)

                return ProgressCardPresenter(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ProgressPresenter, position: Int) {
        when (indexViewTypeHashMap[position]) {
            ProgressAdapterViewType.Header.ordinal -> {
                val presenter = holder as ProgressHeaderPresenter

                presenter.bindView(repositoryCategory)
            }

            ProgressAdapterViewType.Section.ordinal -> {
                val presenter = holder as ProgressTitlePresenter

                indexSectionHashMap[position]?.let {
                    presenter.bindView(it)
                }
            }

            else -> {
                val presenter = holder as ProgressCardPresenter

                indexExerciseHashMap[position]?.let {
                    presenter.bindView(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return indexViewTypeHashMap.size
    }

    override fun getItemViewType(position: Int): Int {
        return indexViewTypeHashMap[position]!!
    }
}

abstract class ProgressPresenter(itemView: View) : RecyclerView.ViewHolder(itemView)

class ProgressHeaderPresenter(itemView: View) : ProgressPresenter(itemView) {
    fun bindView(repositoryCategory: RepositoryCategory) {
        val numberOfCompletedExercises = RepositoryRoutine.getNumberOfCompletedExercises(repositoryCategory.exercises)
        val numberOfExercises = RepositoryRoutine.getNumberOfExercises(repositoryCategory.exercises)
        val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

        itemView.completed_exercises_value.text = "$numberOfCompletedExercises out of $numberOfExercises"
        itemView.completion_rate_value.text = "${completionRate.label}"
    }
}

class ProgressCardPresenter(itemView: View) : ProgressPresenter(itemView) {
    fun bindView(repositoryExercise: RepositoryExercise) {
        itemView.exercise_title.text = repositoryExercise.title
        itemView.exercise_summary.text = LogWorkoutPresenter().getToolbarDescription(repositoryExercise)

        itemView.view_button.setOnClickListener {
            UiEvent.showDialog(DialogType.ProgressActivityLogWorkout, repositoryExercise.exerciseId)
        }

        itemView.exercise_sets.removeAllViews()

        for ((index, repositorySet) in repositoryExercise.sets.withIndex()) {
            val view = itemView.exercise_sets.inflate(R.layout.activity_progress_card_set)

            if (repositorySet.isTimed) {
                val rawSeconds = repositoryExercise.sets.map { it.seconds }.sum()

                val stringMinutes = rawSeconds.formatMinutes(format = false)
                val numberOfMinutes = rawSeconds.formatMinutesAsNumber()
                val stringSeconds = rawSeconds.formatSeconds(format = false)
                val numberOfSeconds = rawSeconds.formatSecondsAsNumber()

                val minutes = if (numberOfMinutes == 1) { "Minute" } else { "Minutes" }
                val seconds = if (numberOfSeconds == 1) { "Second" } else { "Seconds" }

                if (rawSeconds < 60) {
                    view.left_value.text = "$stringSeconds $seconds"
                } else if (numberOfSeconds == 0 || numberOfSeconds == 60) {
                    view.left_value.text = "$stringMinutes $minutes"
                } else {
                    view.left_value.text = "$stringMinutes $minutes $stringSeconds $seconds"
                }

                view.left_label.text = "Set ${index + 1}"

                view.right_value.text = ""
                view.right_label.text = ""
            } else {
                val reps = if (repositorySet.reps == 1) { "Rep" } else { "Reps" }

                view.left_value.text = "${repositorySet.reps} $reps"
                view.left_label.text = "Set ${index + 1}"

                if (repositorySet.weight > 0.0) {
                    view.right_value.text = "${repositorySet.weight}"
                    view.right_label.text = "Weight"
                } else {
                    view.right_value.text = ""
                    view.right_label.text = ""
                }
            }

            itemView.exercise_sets.addView(view)
        }
    }
}

class ProgressTitlePresenter(itemView: View) : ProgressPresenter(itemView) {
    fun bindView(repositorySection: RepositorySection) {
        if (layoutPosition == 0) {
            itemView.title.setPadding(
                    itemView.title.paddingLeft,
                    itemView.title.paddingLeft,
                    itemView.title.paddingRight,
                    itemView.title.paddingBottom)
        } else {
            itemView.title.setPadding(
                    itemView.title.paddingLeft,
                    itemView.title.paddingBottom,
                    itemView.title.paddingRight,
                    itemView.title.paddingBottom)
        }

        itemView.title.text = repositorySection.title
    }
}
