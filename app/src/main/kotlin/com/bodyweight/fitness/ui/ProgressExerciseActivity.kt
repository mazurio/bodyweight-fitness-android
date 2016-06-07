package com.bodyweight.fitness.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.ActionBar
import com.bodyweight.fitness.*

import com.bodyweight.fitness.adapter.RepsAdapter
import com.bodyweight.fitness.adapter.TimeAdapter
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.repository.Repository

import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.activity_progress_card_set.view.*
import kotlinx.android.synthetic.main.activity_progress_exercise.*

import org.joda.time.DateTime
import rx.android.schedulers.AndroidSchedulers
import java.util.*

class ProgressExerciseActivity : RxAppCompatActivity() {
    val exerciseId: String by lazy {
        intent.getStringExtra(Constants.exerciseId)
    }

    val repositoryExercise: RepositoryExercise? by lazy {
        Repository.realm.where(RepositoryExercise::class.java)
                .equalTo("exerciseId", exerciseId)
                .findFirst()
    }

    val repsAdapter by lazy {
        RepsAdapter()
    }

    val timeAdapter by lazy {
        TimeAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_progress_exercise)

        setSupportActionBar(toolbar)

        supportActionBar?.let {
            val actionBar = it

            it.title = "Exercise History"

            repositoryExercise?.let {
                if (it.defaultSet == "timed") {
                    actionBar.subtitle = "Time Graph"
                } else {
                    actionBar.subtitle = "Reps Graph"
                }
            }

            it.elevation = 0f
            it.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        graph_view.baseLineColor = Color.WHITE
        graph_view.scrubLineColor = Color.parseColor("#111111")
        graph_view.isScrubEnabled = true
        graph_view.animateChanges = true
        graph_view.setScrubListener {
            val data = it as? DateTimeRepositorySet

            data?.let {
                updateTitle(it)
            }
        }

        repositoryExercise?.let {
            if (it.defaultSet == "timed") {
                graph_view.adapter = timeAdapter

                updateGraph(timeAdapter)
            } else {
                graph_view.adapter = repsAdapter

                updateGraph(repsAdapter)
            }
        }

        updateList()
    }

    fun updateTitle(data: DateTimeRepositorySet) {
        graph_title.text = data.dateTime.toString("dd MMMM, YYYY", Locale.ENGLISH)

        if (data.repositorySet != null) {
            if (data.repositorySet.isTimed) {
                val rawSeconds = data.repositorySet.seconds

                val stringMinutes = rawSeconds.formatMinutes(format = false)
                val numberOfMinutes = rawSeconds.formatMinutesAsNumber()
                val stringSeconds = rawSeconds.formatSeconds(format = false)
                val numberOfSeconds = rawSeconds.formatSecondsAsNumber()

                val minutes = if (numberOfMinutes == 1) { "Minute" } else { "Minutes" }
                val seconds = if (numberOfSeconds == 1) { "Second" } else { "Seconds" }

                if (rawSeconds < 60) {
                    graph_description.text = "$stringSeconds $seconds"
                } else if (numberOfSeconds == 0 || numberOfSeconds == 60) {
                    graph_description.text = "$stringMinutes $minutes"
                } else {
                    graph_description.text = "$stringMinutes $minutes $stringSeconds $seconds"
                }
            } else {
                val reps = if (data.repositorySet.reps == 1) { "Rep" } else { "Reps" }

                graph_description.text = "${data.repositorySet.reps} $reps"
            }
        } else {
            graph_description.text = "Not Completed"
        }
    }

    fun updateGraph(adapter: RepsAdapter) {
        Repository.realm.where(RepositoryExercise::class.java)
                .equalTo("exerciseId", exerciseId)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)
                .subscribe {
                    if (it.isNotEmpty()) {
                        val list = ArrayList<DateTimeRepositorySet>()

                        for (repositoryExercise in it) {
                            val date = DateTime(repositoryExercise.routine?.startTime)

                            for (repositorySet in repositoryExercise.sets.filter { !it.isTimed }) {
                                list.add(DateTimeRepositorySet(date, repositorySet))
                            }
                        }

                        adapter.changeData(list)

                        if (list.size > 1) {
                            updateTitle(list.first())
                        } else {
                            graph_card_view.setGone()
                        }
                    } else {
                        graph_card_view.setGone()
                    }
                }
    }

    fun updateGraph(adapter: TimeAdapter) {
        Repository.realm.where(RepositoryExercise::class.java)
                .equalTo("exerciseId", exerciseId)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)
                .subscribe {
                    if (it.isNotEmpty()) {
                        val list = ArrayList<DateTimeRepositorySet>()

                        for (repositoryExercise in it) {
                            val date = DateTime(repositoryExercise.routine?.startTime)

                            for (repositorySet in repositoryExercise.sets.filter { it.isTimed }) {
                                list.add(DateTimeRepositorySet(date, repositorySet))
                            }
                        }

                        adapter.changeData(list)

                        if (list.size > 1) {
                            updateTitle(list.first())
                        } else {
                            graph_card_view.setGone()
                        }
                    } else {
                        graph_card_view.setGone()
                    }
                }
    }

    fun updateList() {
        Repository.realm.where(RepositoryExercise::class.java)
                .equalTo("exerciseId", exerciseId)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)
                .subscribe {
                    if (it.isNotEmpty()) {
                        it.firstOrNull()?.let {
                            exercise_title.text = it.title
                            exercise_description.text = "Data"
                        }

                        exercise_sets.removeAllViews()

                        for (repositoryExercise in it) {
                            val date = DateTime(repositoryExercise.routine?.startTime).toString("d MMMM YYYY")

                            for ((index, repositorySet) in repositoryExercise.sets.withIndex()) {
                                val view = exercise_sets.inflate(R.layout.activity_progress_card_set)

                                if (repositorySet.isTimed) {
                                    val rawSeconds = repositorySet.seconds

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

                                    view.left_label.text = "$date - Set ${index + 1}"

                                    view.right_value.text = ""
                                    view.right_label.text = ""
                                } else {
                                    val reps = if (repositorySet.reps == 1) { "Rep" } else { "Reps" }

                                    view.left_value.text = "${repositorySet.reps} $reps"
                                    view.left_label.text = "$date - Set ${index + 1}"

                                    if (repositorySet.weight > 0.0) {
                                        view.right_value.text = "${repositorySet.weight}"
                                        view.right_label.text = "Weight"
                                    } else {
                                        view.right_value.text = ""
                                        view.right_label.text = ""
                                    }
                                }

                                exercise_sets.addView(view)
                            }
                        }
                    }
                }
    }
}