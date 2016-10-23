package com.bodyweight.fitness.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.bodyweight.fitness.Constants

import com.bodyweight.fitness.R
import com.bodyweight.fitness.dialog.LogWorkoutDialog
import com.bodyweight.fitness.dialog.ProgressDialog
import com.bodyweight.fitness.model.DialogType
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.stream.UiEvent
import com.bodyweight.fitness.utils.Preferences

import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindUntilEvent

import kotlinx.android.synthetic.main.activity_dashboard.*

class WorkoutActivity : RxAppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, WorkoutActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_workout)
        setToolbar()

        keepScreenOnWhenAppIsRunning()

        Stream.menuObservable
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .filter { it == R.id.action_dashboard }
                .subscribe {
                    startActivity(Intent(this, DashboardActivity::class.java))
                }

        RoutineStream.exerciseObservable().bindUntilEvent(this, ActivityEvent.DESTROY).subscribe { exercise ->
            supportActionBar?.let { toolbar ->
                toolbar.title = exercise.title

                exercise.section?.let {
                    toolbar.subtitle = it.title + " " + exercise.description
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        keepScreenOnWhenAppIsRunning()

        UiEvent.dialogObservable
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    if (it.dialogType === DialogType.MainActivityLogWorkout) {
                        val bundle = Bundle()
                        bundle.putString(Constants.exerciseId, it.exerciseId)

                        val logWorkoutDialog = LogWorkoutDialog()
                        logWorkoutDialog.arguments = bundle
                        logWorkoutDialog.show(supportFragmentManager, "logWorkoutDialog")
                    } else if (it.dialogType === DialogType.Progress) {
                        val bundle = Bundle()
                        bundle.putString(Constants.exerciseId, it.exerciseId)

                        val progressDialog = ProgressDialog()
                        progressDialog.arguments = bundle
                        progressDialog.show(supportFragmentManager, "progressDialog")
                    }
                }
    }

    override fun onStop() {
        super.onStop()

        clearFlagKeepScreenOn()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Stream.setMenu(item.itemId)

        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_workout, menu)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        keepScreenOnWhenAppIsRunning()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.elevation = 0f
            it.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun keepScreenOnWhenAppIsRunning() {
        if (Preferences.keepScreenOnWhenAppIsRunning()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            clearFlagKeepScreenOn()
        }
    }

    private fun clearFlagKeepScreenOn() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
