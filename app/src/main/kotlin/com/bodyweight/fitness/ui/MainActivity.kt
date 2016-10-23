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

import com.trello.rxlifecycle.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindUntilEvent

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

class MainActivity : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setToolbar()

        val event = ActivityEvent.DESTROY

        Stream.drawerObservable()
                .bindUntilEvent(this, event)
                .subscribe {
                    invalidateOptionsMenu()

                    when (it) {
//                        R.id.action_menu_support_developer -> {
//                            startActivity(Intent(Intent.ACTION_VIEW).apply {
//                                data = Uri.parse(Constants.googlePlayUrl)
//                            })
//                        }
//
//                        R.id.action_menu_settings -> {
//                            startActivity(Intent(applicationContext, SettingsActivity::class.java))
//                        }

                        else -> {
//                            navigation_view.setCheckedItem(it)
                        }
                    }
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
