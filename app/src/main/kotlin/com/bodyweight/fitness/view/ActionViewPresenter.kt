package com.bodyweight.fitness.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.R
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.formatMinutes
import com.bodyweight.fitness.formatSeconds
import com.bodyweight.fitness.stream.*

import com.bodyweight.fitness.ui.ProgressActivity
import com.bodyweight.fitness.utils.ViewUtils
import com.bodyweight.fitness.view.dialog.LogWorkoutDialog
import com.bodyweight.fitness.view.dialog.ProgressDialog
import com.bodyweight.fitness.view.widget.ActionButton
import com.gordonwong.materialsheetfab.MaterialSheetFab
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_action.view.*

object ActionShared {
    var id: Int = R.id.action_menu_home
}

class ActionPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        val view = (mView as ActionView)

        getExerciseObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    if (it.hasProgressions()) {
                        view.setActionButtonImageDrawable(R.drawable.action_progression_white)
                        view.showActionSheetChooseProgression()
                    } else {
                        view.setActionButtonImageDrawable(R.drawable.action_add)
                        view.hideActionSheetChooseProgression()
                    }
                }

        Stream.drawerObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    ActionShared.id = it

                    if (it == R.id.action_menu_home) {
                        view.showActionButtons()
                    } else {
                        view.hideActionButtons()
                    }
                }

        Stream.loggedSecondsObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    val format = String.format("Logged time %s:%s", it.formatMinutes(), it.formatSeconds())

                    Snackbar.make(view.action_view_coordinator_layout, format, Snackbar.LENGTH_LONG).show()
                }

        Stream.loggedSetRepsObservable
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    val format = String.format("Logged Set %d with %d Reps", it.set, it.reps)

                    Snackbar.make(view.action_view_coordinator_layout, format, Snackbar.LENGTH_LONG).show()
                }
    }

    override fun restoreView(view: AbstractView) {
        super.restoreView(view)

        val view = (mView as ActionView)

        if (ActionShared.id == R.id.action_menu_home) {
            view.showActionButtons()
        } else {
            view.hideActionButtons()
        }
    }

    fun logWorkout() {
        UiEvent.showDialog(DialogType.LogWorkout, getCurrentExercise().exerciseId)
    }

    fun watchFullVideo() {
        val view = (mView as ActionView)
        val id = RoutineStream.getInstance().exercise.youTubeId

        try {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id)))
        } catch (e: ActivityNotFoundException) {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id)))
        }
    }

    fun chooseProgression() {
        UiEvent.showDialog(DialogType.Progress, getCurrentExercise().exerciseId)
    }

    fun todaysWorkout() {
        val view = (mView as ActionView)
        val routineId = RepositoryStream.getInstance().repositoryRoutineForToday.id

        view.context.startActivity(Intent(view.context, ProgressActivity::class.java)
                .putExtra(Constants.PRIMARY_KEY_ROUTINE_ID, routineId))
    }
}

open class ActionView : AbstractView {
    override var mPresenter: AbstractPresenter = ActionPresenter()

    internal var mMaterialSheet: MaterialSheetFab<ActionButton>? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        val presenter = (mPresenter as ActionPresenter)

        ViewUtils.resetFloatingActionButtonMargin(action_view_log_workout_button)
        ViewUtils.resetFloatingActionButtonMargin(action_view_action_button, 0, 0, 12, 12)

        mMaterialSheet = MaterialSheetFab<ActionButton>(
                action_view_action_button,
                action_view_action_sheet,
                action_view_overlay,
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#FFFDDD"))

        mMaterialSheet?.setEventListener(object : MaterialSheetFabEventListener() {
            override fun onShowSheet() {
                super.onShowSheet()

                action_view_log_workout_button.hide()
            }

            override fun onSheetShown() {
                super.onSheetShown()
            }

            override fun onHideSheet() {
                super.onHideSheet()
            }

            override fun onSheetHidden() {
                super.onSheetHidden()

                action_view_log_workout_button.show()
            }
        })

        action_view_log_workout_button.setOnClickListener {
            presenter.logWorkout()
        }

        action_view_action_sheet_watch_on_youtube.setOnClickListener {
            mMaterialSheet?.hideSheet()
            presenter.watchFullVideo()
        }

        action_view_action_sheet_choose_progression.setOnClickListener {
            mMaterialSheet?.hideSheet()
            presenter.chooseProgression()
        }

        action_view_action_sheet_todays_workout.setOnClickListener {
            mMaterialSheet?.hideSheet()
            presenter.todaysWorkout()
        }
    }

    fun showActionButtons() {
        action_view_log_workout_button.visibility = View.VISIBLE
        action_view_action_button.visibility = View.VISIBLE
    }

    fun hideActionButtons() {
        action_view_log_workout_button.visibility = View.GONE
        action_view_action_button.visibility = View.GONE
    }

    fun showActionSheetChooseProgression() {
        action_view_action_sheet_choose_progression.visibility = View.VISIBLE
    }

    fun hideActionSheetChooseProgression() {
        action_view_action_sheet_choose_progression.visibility = View.GONE
    }

    fun setActionButtonImageDrawable(drawable: Int) {
        action_view_action_button.setImageDrawable(resources.getDrawable(drawable))
    }
}