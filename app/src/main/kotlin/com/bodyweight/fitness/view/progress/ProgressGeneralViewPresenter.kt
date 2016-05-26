package com.bodyweight.fitness.view.progress

import android.content.Context
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import com.bodyweight.fitness.*
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.DateTimeWorkoutLength

import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.view.AbstractPresenter
import com.bodyweight.fitness.view.AbstractView
import com.robinhood.spark.SparkAdapter
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_progress_general.view.*
import kotlinx.android.synthetic.main.activity_progress_general_exercise.view.*

import org.joda.time.DateTime
import rx.android.schedulers.AndroidSchedulers

import java.util.*

import kotlin.properties.Delegates

class WorkoutLengthAdapter : SparkAdapter() {
    private var data = ArrayList<DateTimeWorkoutLength>()

    fun changeData(data: ArrayList<DateTimeWorkoutLength>) {
        this.data = data

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): Any {
        val item = data.getOrNull(index)

        item?.let {
            return it
        }

        return ""
    }

    override fun getY(index: Int): Float {
        data[index].repositoryRoutine?.let {
            return RepositoryRoutine.getWorkoutLengthInMinutes(it).toFloat()
        }

        return 0f
    }

    override fun getX(index: Int): Float {
        return index.toFloat()
    }
}

class ProgressGeneralViewPresenter : AbstractPresenter() {
    var repositoryRoutine: RepositoryRoutine by Delegates.notNull()

    val exercises by lazy {
        getVisibleAndCompletedExercises(repositoryRoutine.exercises)
    }

    val missedExercises by lazy {
        getMissedExercises(repositoryRoutine.exercises)
    }

    val numberOfExercises by lazy {
        getNumberOfExercises(exercises)
    }

    val numberOfCompletedExercises by lazy {
        getNumberOfCompletedExercises(exercises)
    }

    val routineCompletionRate by lazy {
        getCompletionRateForRoutine(repositoryRoutine)
    }

    override fun updateView() {
        super.updateView()

        renderTime()
        renderTodaysProgress()
        renderMissedExercises()
        renderWorkoutLengthHistoryGraph()
    }

    fun renderTime() {
        val view = getView() as ProgressGeneralView

        view.start_time_value.text = RepositoryRoutine.getStartTime(repositoryRoutine)
        view.end_time_value.text = RepositoryRoutine.getLastUpdatedTime(repositoryRoutine)
        view.workout_length_value.text = RepositoryRoutine.getWorkoutLength(repositoryRoutine)

        if (routineCompletionRate.percentage == 100) {
            view.end_time_label.text = "End Time"
        } else {
            view.end_time_label.text = "Last Updated"
        }
    }

    fun renderTodaysProgress() {
        val view = getView() as ProgressGeneralView

        view.completed_exercises_value.text = "$numberOfCompletedExercises out of $numberOfExercises"
        view.completion_rate_value.text = "${routineCompletionRate.percentage}%"

        repositoryRoutine.categories.getOrNull(0)?.let {
            val completionRate = getCompletionRateForCategory(true, it)

            view.setCategoryOne(it.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
        }

        repositoryRoutine.categories.getOrNull(1)?.let {
            val completionRate = getCompletionRateForCategory(true, it)

            view.setCategoryTwo(it.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
        }

        repositoryRoutine.categories.getOrNull(2)?.let {
            val completionRate = getCompletionRateForCategory(true, it)

            view.setCategoryThree(it.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
        }
    }

    fun renderMissedExercises() {
        val view = getView() as ProgressGeneralView
        val parent = view.missed_exercises_layout

        if (missedExercises.isNotEmpty()) {
            view.missed_exercises_title.setVisible()
            view.missed_exercises_card.setVisible()

            for (exercise in missedExercises) {
                val layout = parent.inflate(R.layout.activity_progress_general_exercise)

                layout.exercise_title.text = exercise.title
                layout.category_title.text = exercise.category?.title

                parent.addView(layout)
            }
        } else {
            view.missed_exercises_title.setGone()
            view.missed_exercises_card.setGone()
        }
    }

    fun renderWorkoutLengthHistoryGraph() {
        val view = getView() as ProgressGeneralView

        val workoutLengthGraphView = view.graph_workout_length_view
        val workoutLengthTabLayout = view.graph_workout_length_tablayout

        val workoutLengthAdapter = WorkoutLengthAdapter()

        workoutLengthGraphView.adapter = workoutLengthAdapter
        workoutLengthGraphView.scrubLineColor = Color.parseColor("#111111")
        workoutLengthGraphView.isScrubEnabled = true
        workoutLengthGraphView.animateChanges = true

        workoutLengthGraphView.setScrubListener {
            val dateTimeWorkoutLength = it as? DateTimeWorkoutLength

            dateTimeWorkoutLength?.let {
                view.graph_workout_length_title.text = it.dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH)

                if (it.repositoryRoutine != null) {
                    view.graph_workout_length_value.text = RepositoryRoutine.getWorkoutLength(it.repositoryRoutine)
                } else {
                    view.graph_workout_length_value.text = "Not Completed"
                }
            }
        }

        workoutLengthTabLayout.addTab(workoutLengthTabLayout.newTab().setText("1W"))
        workoutLengthTabLayout.addTab(workoutLengthTabLayout.newTab().setText("1M"))
        workoutLengthTabLayout.addTab(workoutLengthTabLayout.newTab().setText("3M"))
        workoutLengthTabLayout.addTab(workoutLengthTabLayout.newTab().setText("6M"))
        workoutLengthTabLayout.addTab(workoutLengthTabLayout.newTab().setText("1Y"))

        workoutLengthTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateWorkoutLengthTitle()

                when (tab.position) {
                    0 -> updateWorkoutLengthGraph(workoutLengthAdapter, 7)
                    1 -> updateWorkoutLengthGraph(workoutLengthAdapter, 30)
                    2 -> updateWorkoutLengthGraph(workoutLengthAdapter, 90)
                    3 -> updateWorkoutLengthGraph(workoutLengthAdapter, 180)
                    else -> updateWorkoutLengthGraph(workoutLengthAdapter, 360)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        updateWorkoutLengthGraph(workoutLengthAdapter, 7)
        updateWorkoutLengthTitle()
    }

    fun updateWorkoutLengthTitle() {
        val view = getView() as ProgressGeneralView

        view.graph_workout_length_title.text = DateTime(repositoryRoutine.startTime).toString("dd MMMM, YYYY", Locale.ENGLISH)
        view.graph_workout_length_value.text = "${RepositoryRoutine.getWorkoutLength(repositoryRoutine)}"
    }

    fun updateWorkoutLengthGraph(adapter: WorkoutLengthAdapter, minusDays: Int = 7) {
        val start = DateTime.now().withTimeAtStartOfDay().minusDays(minusDays)
        val end = DateTime.now()

        Repository.realm.where(RepositoryRoutine::class.java)
                .between("startTime", start.toDate(), end.toDate())
                .findAllAsync()
                .sort("startTime", Sort.DESCENDING)
                .asObservable()
                .filter { it.isLoaded }
                .map {
                    val dates = ArrayList<DateTimeWorkoutLength>()

                    for (index in 1..minusDays) {
                        val date = start.plusDays(index)

                        val repositoryRoutine = it.filter {
                            val startTime = DateTime(it.startTime)

                            date.dayOfMonth == startTime.dayOfMonth
                                    && date.monthOfYear == startTime.monthOfYear
                                    && date.year == startTime.year
                        }.firstOrNull()

                        if (repositoryRoutine != null) {
                            dates.add(DateTimeWorkoutLength(date, repositoryRoutine))
                        } else {
                            dates.add(DateTimeWorkoutLength(date, null))
                        }
                    }

                    dates
                }
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(getView())
                .subscribe {
                    adapter.changeData(it)
                }
    }
}

open class ProgressGeneralView : AbstractView {
    override var presenter: AbstractPresenter = ProgressGeneralViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        super.onCreateView()
    }

    fun setCategoryOne(title: String, completionRateLabel: String, completionRateValue: Float) {
        category_one_title.text = title
        category_one_completion_rate_label.text = completionRateLabel
        category_one_completion_rate_value.setLayoutWeight(completionRateValue)
    }

    fun setCategoryTwo(title: String, completionRateLabel: String, completionRateValue: Float) {
        category_two_title.text = title
        category_two_completion_rate_label.text = completionRateLabel
        category_two_completion_rate_value.setLayoutWeight(completionRateValue)
    }

    fun setCategoryThree(title: String, completionRateLabel: String, completionRateValue: Float) {
        category_three_title.text = title
        category_three_completion_rate_label.text = completionRateLabel
        category_three_completion_rate_value.setLayoutWeight(completionRateValue)
    }


}