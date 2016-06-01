package com.bodyweight.fitness.adapter

import android.content.Intent
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bodyweight.fitness.*

import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositorySection
import com.bodyweight.fitness.stream.DialogType
import com.bodyweight.fitness.stream.UiEvent
import com.bodyweight.fitness.dialog.LogWorkoutPresenter
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.ui.ProgressActivity
import com.bodyweight.fitness.ui.ProgressExerciseActivity
import com.bodyweight.fitness.view.progress.CategoryCompletionRateAdapter

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_progress_card.view.*
import kotlinx.android.synthetic.main.activity_progress_card_set.view.*
import kotlinx.android.synthetic.main.activity_progress_header.view.*
import kotlinx.android.synthetic.main.activity_progress_title.view.*
import org.joda.time.DateTime
import rx.android.schedulers.AndroidSchedulers
import java.util.*

enum class ProgressAdapterViewType {
    Header,
    Section,
    Exercise
}

class ProgressListAdapter(private val repositoryCategory: RepositoryCategory) : RecyclerView.Adapter<ProgressPresenter>() {
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
    var repositoryCategory: RepositoryCategory? = null

    init {
        val completionRateGraphView = itemView.graph_category_completion_rate_view
        val completionRateTabLayout = itemView.graph_category_completion_rate_tablayout

        completionRateGraphView.scrubLineColor = Color.parseColor("#111111")
        completionRateGraphView.isScrubEnabled = true
        completionRateGraphView.animateChanges = true

        completionRateTabLayout.addTab(completionRateTabLayout.newTab().setText("1W"))
        completionRateTabLayout.addTab(completionRateTabLayout.newTab().setText("1M"))
        completionRateTabLayout.addTab(completionRateTabLayout.newTab().setText("3M"))
        completionRateTabLayout.addTab(completionRateTabLayout.newTab().setText("6M"))
        completionRateTabLayout.addTab(completionRateTabLayout.newTab().setText("1Y"))
    }

    fun bindView(repositoryCategory: RepositoryCategory) {
        this.repositoryCategory = repositoryCategory

        val numberOfCompletedExercises = RepositoryRoutine.getNumberOfCompletedExercises(repositoryCategory.exercises)
        val numberOfExercises = RepositoryRoutine.getNumberOfExercises(repositoryCategory.exercises)
        val completionRate = RepositoryCategory.getCompletionRate(repositoryCategory)

        itemView.completed_exercises_value.text = "$numberOfCompletedExercises out of $numberOfExercises"
        itemView.completion_rate_value.text = "${completionRate.label}"

        val completionRateGraphView = itemView.graph_category_completion_rate_view
        val completionRateTabLayout = itemView.graph_category_completion_rate_tablayout

        val completionRateAdapter = CategoryCompletionRateAdapter()

        completionRateGraphView.adapter = completionRateAdapter
        completionRateGraphView.setScrubListener {
            val dateTimeCompletionRate = it as? CategoryDateTimeCompletionRate

            dateTimeCompletionRate?.let {
                itemView.graph_category_completion_rate_title.text = it.dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH)

                if (it.repositoryCategory != null) {
                    val completionRate = RepositoryCategory.getCompletionRate(it.repositoryCategory)

                    itemView.graph_category_completion_rate_description.text = "${completionRate.label}"
                } else {
                    itemView.graph_category_completion_rate_description.text = "Not Completed"
                }
            }
        }

        completionRateTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateCompletionRateTitle()

                when (tab.position) {
                    0 -> updateCompletionRateGraph(completionRateAdapter, 7)
                    1 -> updateCompletionRateGraph(completionRateAdapter, 30)
                    2 -> updateCompletionRateGraph(completionRateAdapter, 90)
                    3 -> updateCompletionRateGraph(completionRateAdapter, 180)
                    else -> updateCompletionRateGraph(completionRateAdapter, 360)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        updateCompletionRateGraph(completionRateAdapter, 7)
        updateCompletionRateTitle()
    }

    fun updateCompletionRateTitle() {
        repositoryCategory?.let {
            val completionRate = RepositoryCategory.getCompletionRate(it)

            itemView.graph_category_completion_rate_title.text = DateTime(it.routine!!.startTime).toString("dd MMMM, YYYY", Locale.ENGLISH)
            itemView.graph_category_completion_rate_description.text = "${completionRate.label}"
        }
    }

    fun updateCompletionRateGraph(adapter: CategoryCompletionRateAdapter, minusDays: Int = 7) {
        val start = DateTime.now().withTimeAtStartOfDay().minusDays(minusDays)
        val end = DateTime.now()

        Repository.realm.where(RepositoryRoutine::class.java)
                .between("startTime", start.toDate(), end.toDate())
                .findAllAsync()
                .sort("startTime", Sort.DESCENDING)
                .asObservable()
                .filter { it.isLoaded }
                .map {
                    val dates = ArrayList<CategoryDateTimeCompletionRate>()

                    for (index in 1..minusDays) {
                        val date = start.plusDays(index)

                        val repositoryCategory: RepositoryCategory? = it.filter {
                            val startTime = DateTime(it.startTime)

                            date.dayOfMonth == startTime.dayOfMonth
                                    && date.monthOfYear == startTime.monthOfYear
                                    && date.year == startTime.year
                        }.firstOrNull()?.categories?.filter {
                            it.categoryId == repositoryCategory?.categoryId
                        }?.firstOrNull()

                        if (repositoryCategory != null) {
                            dates.add(CategoryDateTimeCompletionRate(date, repositoryCategory))
                        } else {
                            dates.add(CategoryDateTimeCompletionRate(date, null))
                        }
                    }

                    dates
                }
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(itemView)
                .subscribe {
                    adapter.changeData(it)
                }
    }
}

class ProgressCardPresenter(itemView: View) : ProgressPresenter(itemView) {
    fun bindView(repositoryExercise: RepositoryExercise) {
        itemView.exercise_title.text = repositoryExercise.title
        itemView.exercise_summary.text = LogWorkoutPresenter().getToolbarDescription(repositoryExercise)

        itemView.full_report_button.setOnClickListener {
            val intent = Intent(it.context, ProgressExerciseActivity::class.java)
            intent.putExtra(Constants.exerciseId, repositoryExercise.exerciseId)

            it.context.startActivity(intent)
        }

        itemView.edit_button.setOnClickListener {
            UiEvent.showDialog(DialogType.ProgressActivityLogWorkout, repositoryExercise.exerciseId)
        }

        itemView.exercise_sets.removeAllViews()

        if (RepositoryExercise.isCompleted(repositoryExercise)) {
            itemView.exercise_sets.setVisible()
        } else {
            itemView.exercise_sets.setGone()
        }

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
