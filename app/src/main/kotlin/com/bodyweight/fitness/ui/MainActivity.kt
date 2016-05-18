package com.bodyweight.fitness.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle

import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager

import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.R
import com.bodyweight.fitness.dialog.LogWorkoutDialog
import com.bodyweight.fitness.dialog.ProgressDialog
import com.bodyweight.fitness.extension.debug
import com.bodyweight.fitness.setGone
import com.bodyweight.fitness.setVisible
import com.bodyweight.fitness.stream.DialogType
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.stream.UiEvent
import com.bodyweight.fitness.utils.Preferences

import com.kobakei.ratethisapp.RateThisApp
import com.trello.rxlifecycle.ActivityEvent

import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindUntilEvent

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_home.*
import kotlinx.android.synthetic.main.view_timer.*
import kotlinx.android.synthetic.main.view_toolbar.*

class MainActivity : RxAppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setToolbar()
        setTabLayout()
        keepScreenOnWhenAppIsRunning()

        val event = ActivityEvent.DESTROY

        UiEvent.dialogObservable
                .bindUntilEvent(this, event)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    if (it.dialogType === DialogType.LogWorkout) {
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

        RoutineStream.exerciseObservable()
                .bindUntilEvent(this, event)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    if (it.isTimedSet) {
                        if (view_tabs.tabCount == 2) {
                            view_tabs.removeAllTabs()
                            view_tabs.addTab(view_tabs.newTab().setText("Timer"))
                        }

                        view_tabs.getTabAt(0)?.select()
                    } else {
                        if (view_tabs.tabCount == 1) {
                            view_tabs.removeAllTabs()

                            view_tabs.addTab(view_tabs.newTab().setText("Timer"))
                            view_tabs.addTab(view_tabs.newTab().setText("Reps Logger"))
                        }

                        view_tabs.getTabAt(1)?.select()
                    }
                }

        Stream.menuObservable
                .bindUntilEvent(this, event)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .filter { it == R.id.action_dashboard }
                .subscribe {
                    startActivity(Intent(this, DashboardActivity::class.java))
                }

        Stream.drawerObservable()
                .bindUntilEvent(this, event)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    when (it) {
                        R.id.action_menu_support_developer -> {
                            startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(Constants.googlePlayUrl)
                            })
                        }

                        R.id.action_menu_settings -> {
                            startActivity(Intent(applicationContext, SettingsActivity::class.java))
                        }
                    }
                }
    }

    override fun onResume() {
        super.onResume()

        keepScreenOnWhenAppIsRunning()
    }

    override fun onStart() {
        super.onStart()

        RateThisApp.onStart(this)
        RateThisApp.showRateDialogIfNeeded(this)
    }

    override fun onStop() {
        super.onStop()

        clearFlagKeepScreenOn()

        Repository.realm.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Stream.setMenu(item.itemId)

        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        keepScreenOnWhenAppIsRunning()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (Stream.currentDrawerId == R.id.action_menu_home) {
            menuInflater.inflate(R.menu.home, menu)
        } else if (Stream.currentDrawerId  == R.id.action_menu_workout_log) {
            menuInflater.inflate(R.menu.calendar, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.elevation = 0f
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        val actionBarDrawerToggle = ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        drawer_layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        navigation_view.setNavigationItemSelectedListener { item ->
            drawer_layout.closeDrawers()

            Stream.setDrawer(item.itemId)

            if (item.itemId == R.id.action_menu_support_developer || item.itemId == R.id.action_menu_settings) {
                return@setNavigationItemSelectedListener false
            } else {
                return@setNavigationItemSelectedListener true
            }
        }
    }

    private fun setTabLayout() {
        view_tabs.addTab(view_tabs.newTab().setText("Timer"))
        view_tabs.addTab(view_tabs.newTab().setText("Reps Logger"))

        view_tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position

                if (position == 0) {
                    timer_view.setVisible()
                    reps_logger_view.setGone()
                } else {
                    timer_view.setGone()
                    reps_logger_view.setVisible()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
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
