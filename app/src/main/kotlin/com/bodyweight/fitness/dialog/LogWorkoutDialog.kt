package com.bodyweight.fitness.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.bodyweight.fitness.*
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences
import com.bodyweight.fitness.view.listener.RepeatListener

import kotlinx.android.synthetic.main.view_dialog_log_workout.view.*

import org.joda.time.DateTime
import org.joda.time.Duration

import java.util.*

import kotlin.properties.Delegates

class LogWorkoutPresenter {
    fun getPreviousWorkoutDescription(repositoryExercise: RepositoryExercise): String {
        if (!RepositoryExercise.isCompleted(repositoryExercise)) {
            return "Not Completed"
        }

        val numberOfSets = repositoryExercise.sets.size
        val numberOfReps = repositoryExercise.sets.map { it.reps }.sum()

        val rawSeconds = repositoryExercise.sets.map { it.seconds }.sum()

        val stringMinutes = rawSeconds.formatMinutes(format = false)
        val numberOfMinutes = rawSeconds.formatMinutesAsNumber()
        val stringSeconds = rawSeconds.formatSeconds(format = false)
        val numberOfSeconds = rawSeconds.formatSecondsAsNumber()

        val sets = if (numberOfSets == 1) { "Set" } else { "Sets" }
        val reps = if (numberOfReps == 1) { "Rep" } else { "Reps" }

        val minutes = if (numberOfMinutes == 1) { "Minute" } else { "Minutes" }
        val seconds = if (numberOfSeconds == 1) { "Second" } else { "Seconds" }

        if (numberOfSets == 1) {
            if (repositoryExercise.defaultSet == "timed") {
                if (rawSeconds < 60) {
                    return "$numberOfSets $sets, $stringSeconds $seconds"
                } else if (numberOfSeconds == 60) {
                    return "$numberOfSets $sets, $stringMinutes $minutes"
                } else if (numberOfSeconds == 0) {
                    return "$numberOfSets $sets, $stringMinutes $minutes"
                } else {
                    return "$numberOfSets $sets, $stringMinutes $minutes $stringSeconds $seconds"
                }
            }

            return "1 Set, $numberOfReps $reps"
        } else {
            if (repositoryExercise.defaultSet == "timed") {
                var t = ""

                for (set in repositoryExercise.sets) {
                    if (repositoryExercise.sets.last() == set) {
                        t += "${set.seconds}s"
                    } else {
                        t += "${set.seconds}s-"
                    }
                }

                return t
            }

            var t = ""

            for (set in repositoryExercise.sets) {
                if (repositoryExercise.sets.last() == set) {
                    t += "${set.reps}"
                } else {
                    t += "${set.reps}-"
                }
            }

            return t
        }

        return "";
    }

    fun getToolbarDescription(repositoryExercise: RepositoryExercise): String {
        if (!RepositoryExercise.isCompleted(repositoryExercise)) {
            return "Not Completed"
        }

        val numberOfSets = repositoryExercise.sets.size
        val numberOfReps = repositoryExercise.sets.map { it.reps }.sum()

        val rawSeconds = repositoryExercise.sets.map { it.seconds }.sum()

        val stringMinutes = rawSeconds.formatMinutes(format = false)
        val numberOfMinutes = rawSeconds.formatMinutesAsNumber()
        val stringSeconds = rawSeconds.formatSeconds(format = false)
        val numberOfSeconds = rawSeconds.formatSecondsAsNumber()

        val sets = if (numberOfSets == 1) { "Set" } else { "Sets" }
        val reps = if (numberOfReps == 1) { "Rep" } else { "Reps" }

        val minutes = if (numberOfMinutes == 1) { "Minute" } else { "Minutes" }
        val seconds = if (numberOfSeconds == 1) { "Second" } else { "Seconds" }

        if (repositoryExercise.defaultSet == "timed") {
            if (rawSeconds < 60) {
                return "$numberOfSets $sets, $stringSeconds $seconds"
            } else if (numberOfSeconds == 0) {
                return "$numberOfSets $sets, $stringMinutes $minutes"
            }

            return "$numberOfSets $sets, $stringMinutes $minutes, $stringSeconds $seconds"
        }

        return "$numberOfSets $sets, $numberOfReps $reps"
    }
}

class LogWorkoutDialog : BottomSheetDialogFragment() {
    private val initialInterval = 400
    private val normalInterval = 100

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private val logWorkoutPresenter: LogWorkoutPresenter = LogWorkoutPresenter()
    private val repositoryRoutine: RepositoryRoutine by lazy {
        val primaryKeyRoutineId = arguments.getString(Constants.primaryKeyRoutineId)

        if (primaryKeyRoutineId == null) {
            Repository.repositoryRoutineForToday
        } else {
            Repository.getRepositoryRoutineForPrimaryKeyRoutineId(primaryKeyRoutineId)
        }
    }

    private val repositoryExercise: RepositoryExercise by lazy {
        val exerciseId = arguments.getString(Constants.exerciseId)

        repositoryRoutine.exercises.filter { it.exerciseId.equals(exerciseId) }.first()
    }

    private var repositorySet: RepositorySet by Delegates.notNull()

    private var layout: View by Delegates.notNull()
    private var rowLayout: LinearLayout by Delegates.notNull()
    private val viewSets: ArrayList<View> = ArrayList()

    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        layout = View.inflate(context, R.layout.view_dialog_log_workout, null)
        dialog?.setContentView(layout)

        val params = ((layout.parent as View).layoutParams as CoordinatorLayout.LayoutParams)
        val behavior = params.behavior

        if (behavior is BottomSheetBehavior) {
            behavior.peekHeight = 400.toPx(context)
            behavior.setBottomSheetCallback(bottomSheetCallback)
        }

        layout.toolbar.title = repositoryExercise.title
        layout.toolbar.subtitle = LogWorkoutPresenter().getToolbarDescription(repositoryExercise)

        layout.repsIncrease.setOnTouchListener(RepeatListener(initialInterval, normalInterval, {
            increaseLeft()
        }));

        layout.repsDecrease.setOnTouchListener(RepeatListener(initialInterval, normalInterval, {
            decreaseLeft()
        }));

        layout.weightIncrease.setOnTouchListener(RepeatListener(initialInterval, normalInterval, {
            increaseRight()
        }));

        layout.weightDecrease.setOnTouchListener(RepeatListener(initialInterval, normalInterval, {
            decreaseRight()
        }));

        val realm = Repository.realm
        val results = realm.where(RepositoryRoutine::class.java)
                .lessThan("startTime", repositoryRoutine.startTime)
                .notEqualTo("id", repositoryRoutine.id)
                .findAll()

        if (results.isNotEmpty()) {
            layout.previous_workout_label.setVisible()
            layout.previous_workout_value.setVisible()
            layout.this_workout_label.setVisible()

            results.last()?.let {
                val exercise = results.last().exercises.filter {
                    it.exerciseId.equals(repositoryExercise.exerciseId)
                }.firstOrNull()

                exercise?.let {
                    layout.previous_workout_value.text = logWorkoutPresenter.getPreviousWorkoutDescription(it)
                }
            }
        } else {
            layout.previous_workout_label.setGone()
            layout.previous_workout_value.setGone()
            layout.this_workout_label.setGone()
        }

        layout.actionView.setGone()
        layout.saveButton.setOnClickListener { dismiss() }

        inflateToolbarMenu()
        buildSets()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        val mode = repositoryExercise.section!!.mode
        if (mode.equals(SectionMode.Levels.asString) || mode.equals(SectionMode.Pick.asString)) {
            Repository.realm.executeTransaction {
                repositoryExercise.isVisible = RepositoryExercise.isCompleted(repositoryExercise)
            }
        }

        Stream.setRepository()
    }

    fun setLastUpdatedTime() {
        val startTime = DateTime(repositoryRoutine.startTime)
        val lastUpdatedTime = DateTime(repositoryRoutine.lastUpdatedTime)
        val duration = Duration(startTime, lastUpdatedTime)

        if (duration.toStandardMinutes().minutes < 120) {
            Repository.realm.executeTransaction {
                repositoryRoutine.lastUpdatedTime = DateTime().toDate()
            }
        }
    }

    fun updateToolbarMenu() {
        layout.toolbar.subtitle = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

        val menu = layout.toolbar.menu

        if(viewSets.size >= Constants.maximumNumberOfSets) {
            menu.findItem(R.id.action_add_set).isVisible = false
            menu.findItem(R.id.action_add_timed_set).isVisible = false
        } else if (viewSets.size == 1) {
            if (repositoryExercise.defaultSet.equals("timed")) {
                menu.findItem(R.id.action_add_set).isVisible = false
                menu.findItem(R.id.action_add_timed_set).isVisible = true
            } else {
                menu.findItem(R.id.action_add_set).isVisible = true
                menu.findItem(R.id.action_add_timed_set).isVisible = false
            }

            menu.findItem(R.id.action_remove_last_set).isVisible = false
        } else {
            if (repositoryExercise.defaultSet.equals("timed")) {
                menu.findItem(R.id.action_add_set).isVisible = false
                menu.findItem(R.id.action_add_timed_set).isVisible = true
            } else {
                menu.findItem(R.id.action_add_set).isVisible = true
                menu.findItem(R.id.action_add_timed_set).isVisible = false
            }

            menu.findItem(R.id.action_remove_last_set).isVisible = true
        }
    }

    fun inflateToolbarMenu() {
        layout.toolbar.inflateMenu(R.menu.menu_log_workout)
        layout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add_set -> {
                    createSet()

                    true
                }
                R.id.action_add_timed_set -> {
                    createSet(isTimed = true)

                    true
                }
                R.id.action_remove_last_set -> {
                    if (shouldRemoveSet()) {
                        removeLastSet()
                        updateToolbarMenu()
                    }

                    true
                }
                else -> false
            }
        }

        updateToolbarMenu()
    }

    fun invalidateToolbarMenu() {
        layout.toolbar.menu.clear()
    }

    fun createSet(isTimed: Boolean = false) {
        if (shouldAddSet()) {
            val lastSet = repositoryExercise.sets.last()
            var set: RepositorySet? = null

            Repository.realm.executeTransaction {
                set = Repository.realm.createObject(RepositorySet::class.java)

                set?.let {
                    it.id = "Set-" + UUID.randomUUID().toString()
                    it.isTimed = isTimed
                    it.seconds = lastSet.seconds
                    it.weight = lastSet.weight
                    it.reps = lastSet.reps
                    it.exercise = repositoryExercise
                }
            }

            set?.let {
                addSet(it, isTimed)
            }

            updateToolbarMenu()
        }
    }

    fun buildSets() {
        for (set in repositoryExercise.sets) {
            if (shouldAddSet()) {
                addSet(set, set.isTimed)
            }
        }

        updateToolbarMenu()
    }

    fun actionViewOpened() {
        invalidateToolbarMenu()

        layout.saveButton.text = "Back"
        layout.saveButton.setOnClickListener {
            updateSets()

            layout.toolbar.subtitle = logWorkoutPresenter.getToolbarDescription(repositoryExercise)

            layout.setView.setVisible()
            layout.actionView.setGone()

            actionViewClosed()
        }
    }

    fun actionViewClosed() {
        inflateToolbarMenu()

        layout.saveButton.text = "Save"
        layout.saveButton.setOnClickListener {
            dismiss()
        }
    }

    fun updateSets() {
        for ((index, view) in viewSets.toArray().withIndex()) {
            val set = repositoryExercise.sets[index]

            updateSet(set, view as View, set.isTimed)
        }
    }

    fun shouldAddSet(): Boolean {
        return viewSets.size < Constants.maximumNumberOfSets
    }

    fun shouldRemoveSet(): Boolean {
        return viewSets.size != 1
    }

    fun addRow() {
        if (listOf(0, 3, 6, 9).contains(viewSets.size)) {
            val view = layout.inflate(R.layout.view_dialog_log_workout_row, layout.setView)

            rowLayout = view as LinearLayout
            layout.setView.addView(view)
        }
    }

    fun addSet(set: RepositorySet, isTimed: Boolean = false) {
        addRow()

        val view = if (isTimed) {
            layout.inflate(R.layout.view_dialog_log_workout_timed_set, layout.setView)
        } else {
            layout.inflate(R.layout.view_dialog_log_workout_set, layout.setView)
        }

        updateSet(set, view, isTimed)

        view.setOnClickListener {
            val index = repositoryExercise.sets.indexOf(set)

            updateActionView(set, index + 1, isTimed)

            layout.setView.visibility = View.GONE
            layout.actionView.visibility = View.VISIBLE

            actionViewOpened()
        }

        rowLayout.addView(view)

        Repository.realm.executeTransaction {
            if (!repositoryExercise.sets.contains(set)) {
                repositoryExercise.sets.add(set)
            }
        }

        viewSets.add(view)

        setLastUpdatedTime();
    }

    fun removeLastSet() {
        Repository.realm.executeTransaction {
            repositoryExercise.sets.remove(repositoryExercise.sets.last())
        }

        viewSets.remove(viewSets.last())
        rowLayout.removeViewAt(rowLayout.childCount - 1)

        if (rowLayout.childCount == 0) {
            layout.setView.removeView(rowLayout)
            rowLayout = layout.setView.getChildAt(layout.setView.childCount - 1) as LinearLayout
        }
    }

    fun updateActionView(repositorySet: RepositorySet, index: Int, isTimed: Boolean = false) {
        if (isTimed) {
            this.repositorySet = repositorySet

            layout.setValue.text = index.toString()

            layout.repsValue.text = repositorySet.seconds.formatMinutes(false)
            layout.repsDescription.text = "Minutes"

            layout.weightValue.text = repositorySet.seconds.formatSeconds(false)
            layout.weightDescription.text = "Seconds"
        } else {
            this.repositorySet = repositorySet

            layout.setValue.text = index.toString()

            layout.repsValue.text = repositorySet.reps.toString()
            layout.repsDescription.text = "Reps"

            layout.weightValue.text = repositorySet.weight.toString()
            layout.weightDescription.text = "Weight (${Preferences.weightMeasurementUnit.asString})"
        }
    }

    fun updateSet(repositorySet: RepositorySet, view: View, isTimed: Boolean = false) {
        if (isTimed) {
            val secondsOnlyValue: TextView = view.findViewById(R.id.secondsOnlyValue) as TextView
            val minutes: TextView = view.findViewById(R.id.minutesValue) as TextView
            val seconds: TextView = view.findViewById(R.id.secondsValue) as TextView
            val center: View = view.findViewById(R.id.center)

            if (repositorySet.seconds < 60) {
                secondsOnlyValue.setVisible()

                minutes.setGone()
                seconds.setGone()
                center.setGone()

                secondsOnlyValue.text = repositorySet.seconds.formatSecondsPostfix()
            } else {
                secondsOnlyValue.setGone()

                minutes.setVisible()
                seconds.setVisible()
                center.setVisible()

                minutes.text = repositorySet.seconds.formatMinutesPostfix()
                seconds.text = repositorySet.seconds.formatSecondsPostfix()
            }
        } else {
            val repsOnlyValue: TextView = view.findViewById(R.id.repsOnlyValue) as TextView
            val reps: TextView = view.findViewById(R.id.repsValue) as TextView
            val weight: TextView = view.findViewById(R.id.weightValue) as TextView
            val center: View = view.findViewById(R.id.center)

            if (repositorySet.weight == 0.0) {
                repsOnlyValue.setVisible()

                reps.setGone()
                weight.setGone()
                center.setGone()

                repsOnlyValue.text = repositorySet.reps.formatReps()
            } else {
                repsOnlyValue.setGone()

                reps.setVisible()
                weight.setVisible()
                center.setVisible()

                reps.text = repositorySet.reps.formatReps(true)
                weight.text = repositorySet.weight.formatWeight()
            }
        }
    }

    fun increaseLeft() {
        if (repositorySet.isTimed) {
            increaseMinutes()
        } else {
            increaseReps()
        }
    }

    fun decreaseLeft() {
        if (repositorySet.isTimed) {
            decreaseMinutes()
        } else {
            decreaseReps()
        }
    }

    fun increaseRight() {
        if (repositorySet.isTimed) {
            increaseSeconds()
        } else {
            increaseWeight()
        }
    }

    fun decreaseRight() {
        if (repositorySet.isTimed) {
            decreaseSeconds()
        } else {
            decreaseWeight()
        }
    }

    fun increaseMinutes() {
        if (repositorySet.seconds / 60 >= 5) {
            return
        }

        Repository.realm.executeTransaction {
            repositorySet.seconds += 60
        }

        layout.repsValue.text = repositorySet.seconds.formatMinutes(false)
        layout.weightValue.text = repositorySet.seconds.formatSeconds(false)

        setLastUpdatedTime()
    }

    fun decreaseMinutes() {
        if (repositorySet.seconds < 60) {
            return
        }

        Repository.realm.executeTransaction {
            repositorySet.seconds -= 60
        }

        layout.repsValue.text = repositorySet.seconds.formatMinutes(false)
        layout.weightValue.text = repositorySet.seconds.formatSeconds(false)

        setLastUpdatedTime()
    }

    fun increaseSeconds() {
        Repository.realm.executeTransaction {
            if (repositorySet.seconds % 60 == 59) {
                repositorySet.seconds -= 59
            } else {
                repositorySet.seconds += 1
            }
        }

        layout.repsValue.text = repositorySet.seconds.formatMinutes(false)
        layout.weightValue.text = repositorySet.seconds.formatSeconds(false)

        setLastUpdatedTime()
    }

    fun decreaseSeconds() {
        Repository.realm.executeTransaction {
            if (repositorySet.seconds % 60 == 0) {
                repositorySet.seconds += 59
            } else {
                repositorySet.seconds -= 1
            }
        }

        layout.repsValue.text = repositorySet.seconds.formatMinutes(false)
        layout.weightValue.text = repositorySet.seconds.formatSeconds(false)

        setLastUpdatedTime()
    }

    fun increaseReps() {
        if (repositorySet.reps >= 50) {
            return
        }

        Repository.realm.executeTransaction {
            repositorySet.reps += 1
        }

        layout.repsValue.text = repositorySet.reps.toString()

        setLastUpdatedTime()
    }

    fun decreaseReps() {
        if (repositorySet.reps == 0) {
            return
        }

        Repository.realm.executeTransaction {
            repositorySet.reps -= 1
        }

        layout.repsValue.text = repositorySet.reps.toString()

        setLastUpdatedTime()
    }

    fun increaseWeight() {
        if (repositorySet.weight >= 250.0) {
            return
        }

        Repository.realm.executeTransaction {
            if (Preferences.weightMeasurementUnit.equals(WeightMeasurementUnit.Kg)) {
                repositorySet.weight += 0.5
            } else {
                repositorySet.weight += 1.0
            }
        }

        layout.weightValue.text = repositorySet.weight.toString()

        setLastUpdatedTime()
    }

    fun decreaseWeight() {
        if (repositorySet.weight <= 0) {
            return
        }

        Repository.realm.executeTransaction {
            if (Preferences.weightMeasurementUnit.equals(WeightMeasurementUnit.Kg)) {
                repositorySet.weight -= 0.5
            } else {
                repositorySet.weight -= 1.0
            }
        }

        layout.weightValue.text = repositorySet.weight.toString()

        setLastUpdatedTime()
    }
}