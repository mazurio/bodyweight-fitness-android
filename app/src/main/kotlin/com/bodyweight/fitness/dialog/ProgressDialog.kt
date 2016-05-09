package com.bodyweight.fitness.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.view.Window

import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.model.SectionMode
import com.bodyweight.fitness.stream.RoutineStream

import com.bodyweight.fitness.R

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_dialog_progress.view.*

class ProgressDialog : DialogFragment() {
    private val exerciseId by lazy {
        arguments.getString(Constants.exerciseId)
    }

    private val exercise by lazy {
        val routine = RoutineStream.getInstance().routine
        routine.exercises.filter { it.exerciseId == exerciseId }.first()
    }

    private val availableProgressions by lazy {
        exercise.section.availableLevels
    }

    private var chosenLevel: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.view_dialog_progress)
        dialog.setCanceledOnTouchOutside(true)

        chosenLevel = exercise.section.currentLevel

        content_view.level_previous_button.setOnClickListener {
            chosenLevel -= 1

            updateDialog()
        }

        content_view.level_next_button.setOnClickListener {
            chosenLevel += 1

            updateDialog()
        }

        content_view.level_confirm_button.setOnClickListener {
            val chosenExercise = exercise.section.exercises[chosenLevel]

            RoutineStream.getInstance().setLevel(chosenExercise, chosenLevel)

            dialog.dismiss()
        }

        content_view.level_progress_bar.setWheelSize(12)
        content_view.level_progress_bar.setProgressColor(Color.parseColor("#009688"))
        content_view.level_progress_bar.setProgressBackgroundColor(Color.parseColor("#00453E"))

        updateDialog()

        return dialog
    }

    private fun updateDialog() {
        val chosenExercise = exercise.section.exercises[chosenLevel]

        content_view.toolbar.title = chosenExercise.title
        content_view.toolbar.subtitle = chosenExercise.description

        if (exercise.section.sectionMode == SectionMode.LEVELS) {
            content_view.level_text_view.text = chosenExercise.level
        } else {
            content_view.level_text_view.text = "Pick One"
        }

        if (chosenLevel == 0) {
            content_view.level_previous_button.visibility = View.INVISIBLE
        } else {
            content_view.level_previous_button.visibility = View.VISIBLE
        }

        if (chosenLevel >= availableProgressions - 1) {
            content_view.level_next_button.visibility = View.INVISIBLE
        } else {
            content_view.level_next_button.visibility = View.VISIBLE
        }

        content_view.level_progress_bar.setProgress(1f / availableProgressions * (chosenLevel + 1))
    }
}
