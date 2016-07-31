package com.bodyweight.fitness.view

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.AttributeSet

import com.bodyweight.fitness.*
import com.bodyweight.fitness.model.RepositoryCategory
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.ui.ProgressActivity

import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_home.view.*
import kotlinx.android.synthetic.main.view_home_category.view.*

import org.joda.time.DateTime

class HomeViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .filter { it.equals(R.id.action_menu_home) }
                .subscribe {
                    updateTodaysProgress()
                    updateStatistics()
                }

        Stream.repositoryObservable
                .bindToLifecycle(view)
                .subscribe {
                    updateTodaysProgress()
                    updateStatistics()
                }

        RoutineStream.routineObservable()
                .bindToLifecycle(view)
                .subscribe {
                    updateTodaysProgress()
                    updateStatistics()
                }
    }

    fun updateTodaysProgress() {
        val view = (getView() as HomeView)

        if (Repository.repositoryRoutineForTodayExists()) {
            view.clearCategories()

            val repositoryRoutine = Repository.repositoryRoutineForToday

            for (category in repositoryRoutine.categories) {
                val completionRate = RepositoryCategory.getCompletionRate(category)

                view.createCategory(category.title, completionRate.label, calculateLayoutWeight(completionRate.percentage))
            }

            view.setStartWorkoutButtonTitle(title = getStartWorkoutButtonText(true))
            view.showTodaysWorkoutLogButton()
        } else {
            view.clearCategories()

            val routine = RoutineStream.routine

            for (category in routine.categories) {
                view.createCategory(category.title, "0%", calculateLayoutWeight(0))
            }

            view.setStartWorkoutButtonTitle(title = getStartWorkoutButtonText(false))
            view.hideTodaysWorkoutLogButton()
        }
    }

    fun updateStatistics() {
        val view = (getView() as HomeView)

        val totalWorkouts = getNumberOfWorkouts()
        val previousWorkoutLabel = getPreviousWorkoutLabel()
        val last7Days = getNumberOfWorkouts(days = 7)
        val last30Days = getNumberOfWorkouts(days = 30)

        view.setNumberOfWorkouts("$totalWorkouts ${getNumberOfWorkoutsPostfix(totalWorkouts)}")
        view.setPreviousWorkout("$previousWorkoutLabel")
        view.setNumberOfWorkoutsLast7Days("$last7Days ${getNumberOfWorkoutsPostfix(last7Days)}")
        view.setNumberOfWorkoutsLast30Days("$last30Days ${getNumberOfWorkoutsPostfix(last30Days)}")
    }

    fun getStartWorkoutButtonText(repositoryRoutineForTodayExists: Boolean): String {
        if (repositoryRoutineForTodayExists) {
            return "Go to Workout"
        }

        return "Start Working Out"
    }

    fun getPreviousWorkoutLabel(): String {
        val startTime = DateTime().withTimeAtStartOfDay()

        val results = Repository.realm.where(RepositoryRoutine::class.java)
                .lessThan("startTime", startTime.toDate())
                .findAll()

        if (results.isNotEmpty()) {
            results.lastOrNull()?.let {
                return getRelativeTime(DateTime(it.startTime), System.currentTimeMillis())
            }
        }

        return "Never"
    }

    fun getRelativeTime(time: DateTime, currentTime: Long): String {
        return DateUtils.getRelativeTimeSpanString(
                time.millis,
                currentTime,
                DateUtils.MINUTE_IN_MILLIS).toString()
    }

    fun getNumberOfWorkouts(): Int {
        return Repository.realm.where(RepositoryRoutine::class.java)
                .count()
                .toInt()
    }

    fun getNumberOfWorkouts(days: Int = 7): Int {
        val start = DateTime()
                .withTimeAtStartOfDay()
                .plusDays(1)
                .minusSeconds(1) // 23:59

        val end = start.minusDays(days) // 23:59 - 7 Days

        return Repository.realm.where(RepositoryRoutine::class.java)
                .between("startTime", end.toDate(), start.toDate())
                .count()
                .toInt()
    }

    fun getNumberOfWorkoutsPostfix(count: Int): String {
        if (count == 1) {
            return "Workout"
        } else {
            return "Workouts"
        }
    }

    fun startWorkout() {
        Stream.setDrawer(R.id.action_menu_workout)
    }

    fun todaysWorkoutLog() {
        val routineId = Repository.repositoryRoutineForToday.id

        context().startActivity(Intent(context(), ProgressActivity::class.java)
                .putExtra(Constants.primaryKeyRoutineId, routineId))
    }
}

open class HomeView : AbstractView {
    override var presenter: AbstractPresenter = HomeViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        super.onCreateView()

        start_workout.setOnClickListener {
            (presenter as HomeViewPresenter).startWorkout()
        }

        todays_workout_log.setOnClickListener {
            (presenter as HomeViewPresenter).todaysWorkoutLog()
        }
    }

    fun setStartWorkoutButtonTitle(title: String) {
        start_workout.text = title
    }

    fun showTodaysWorkoutLogButton() {
        todays_workout_log.setVisible()
    }

    fun hideTodaysWorkoutLogButton() {
        todays_workout_log.setGone()
    }

    fun clearCategories() {
        category.removeAllViews()
    }

    fun createCategory(title: String, completionRateLabel: String, completionRateValue: Float) {
        val new_category = category.inflate(R.layout.view_home_category)

        new_category.title.text = title
        new_category.completion_rate_label.text = completionRateLabel
        new_category.completion_rate_value.setLayoutWeight(completionRateValue)

        category.addView(new_category)
    }

    fun setNumberOfWorkouts(title: String) {
        total_workouts_value.text = title
    }

    fun setPreviousWorkout(title: String) {
        previous_workout_value.text = title
    }

    fun setNumberOfWorkoutsLast7Days(title: String) {
        last_7_days_value.text = title
    }

    fun setNumberOfWorkoutsLast30Days(title: String) {
        last_30_days_value.text = title
    }
}