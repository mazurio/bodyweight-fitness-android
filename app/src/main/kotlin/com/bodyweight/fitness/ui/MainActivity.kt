package com.bodyweight.fitness.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu

import android.view.MenuItem
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.R
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences

import com.kobakei.ratethisapp.RateThisApp
import com.trello.rxlifecycle.android.ActivityEvent

import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindUntilEvent

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

class MainActivity : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setToolbar()

        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.view_settings, SettingsFragment(), "SettingsFragment")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        Stream.drawerObservable()
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe {
                    invalidateOptionsMenu()
                }

        if (!Preferences.introductionShown) {
            Preferences.introductionShown = true

            startActivity(Intent(this, IntroductionActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        RateThisApp.onStart(this)
        RateThisApp.showRateDialogIfNeeded(this)
    }

    override fun onStop() {
        super.onStop()

        Repository.realm.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Stream.setMenu(item.itemId)

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()

        when (Stream.currentDrawerId) {
            R.id.action_menu_workout_log -> menuInflater.inflate(R.menu.menu_log_workout, menu)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.elevation = 0f
        }

        bottomBar.setOnTabSelectListener {
            Stream.setDrawer(it)
        }
    }
}
