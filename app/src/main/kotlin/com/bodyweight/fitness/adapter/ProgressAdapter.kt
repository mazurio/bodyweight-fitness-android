package com.bodyweight.fitness.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositorySection
import com.bodyweight.fitness.stream.DialogType
import com.bodyweight.fitness.stream.UiEvent

import java.util.HashMap

import com.bodyweight.fitness.R
import com.bodyweight.fitness.dialog.LogWorkoutPresenter
import com.bodyweight.fitness.inflate
import com.bodyweight.fitness.model.*

import kotlinx.android.synthetic.main.activity_progress_card.view.*
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
        itemView.toolbar.title = repositoryExercise.title
        itemView.toolbar.subtitle = LogWorkoutPresenter().getToolbarDescription(repositoryExercise)

        itemView.view_button.setOnClickListener {
            UiEvent.showDialog(DialogType.ProgressActivityLogWorkout, repositoryExercise.exerciseId)
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
