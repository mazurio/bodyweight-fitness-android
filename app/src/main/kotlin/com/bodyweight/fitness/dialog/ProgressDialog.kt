package com.bodyweight.fitness.dialog

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View

import com.bodyweight.fitness.*
import com.bodyweight.fitness.model.*
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.RoutineStream

import kotlinx.android.synthetic.main.view_dialog_progress.view.*

import kotlin.properties.Delegates

class ProgressDialog : BottomSheetDialogFragment() {
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private val exercise: Exercise = RoutineStream.exercise
    private val availableLevels: Int = exercise.section!!.availableLevels
    private var chosenLevel: Int = exercise.section!!.currentLevel
    private var layout: View by Delegates.notNull()

    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        layout = View.inflate(context, R.layout.view_dialog_progress, null)
        dialog?.setContentView(layout)

        val params = ((layout.parent as View).layoutParams as CoordinatorLayout.LayoutParams)
        val behavior = params.behavior

        if (behavior is BottomSheetBehavior) {
            behavior.peekHeight = 400.toPx(context)
            behavior.setBottomSheetCallback(bottomSheetCallback)
        }

        layout.level_previous_button.setOnClickListener {
            chosenLevel -= 1
            updateDialog();
        }

        layout.level_next_button.setOnClickListener {
            chosenLevel += 1
            updateDialog();
        }

        layout.level_confirm_button.setOnClickListener {
            val chosenExercise = exercise.section!!.exercises[chosenLevel]

            RoutineStream.setLevel(chosenExercise, chosenLevel)

            if (Repository.repositoryRoutineForTodayExists()) {
                val repositoryRoutine = Repository.repositoryRoutineForToday

                Repository.realm.executeTransaction {
                    repositoryRoutine.exercises.filter { it.exerciseId == exercise.exerciseId }.firstOrNull()?.let {
                        it.visible == false
                    }

                    repositoryRoutine.exercises.filter { it.exerciseId == chosenExercise.exerciseId }.firstOrNull()?.let {
                        it.visible == true
                    }
                }
            }

            dialog?.dismiss()
        }

        layout.level_progress_bar.setWheelSize(12)
        layout.level_progress_bar.setProgressColor(primary())
        layout.level_progress_bar.setProgressBackgroundColor(primaryDark());

        updateDialog()
    }

    fun updateDialog() {
        val chosenExercise = exercise.section!!.exercises[chosenLevel]

        layout.toolbar.title = chosenExercise.title
        layout.toolbar.subtitle = chosenExercise.description

        if (exercise.section!!.sectionMode == SectionMode.Levels) {
            layout.level_text_view.text = chosenExercise.level
        } else {
            layout.level_text_view.text = "Pick One"
        }

        if (chosenLevel == 0) {
            layout.level_previous_button.setInvisible()
        } else {
            layout.level_previous_button.setVisible()
        }

        if (chosenLevel >= (availableLevels - 1)) {
            layout.level_next_button.setInvisible()
        } else {
            layout.level_next_button.setVisible()
        }

        layout.level_progress_bar.setProgress((1f / availableLevels) * (chosenLevel + 1));
    }
}