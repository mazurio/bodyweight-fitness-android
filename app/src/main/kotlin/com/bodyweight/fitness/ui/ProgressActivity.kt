package com.bodyweight.fitness.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.ActionBar
import android.view.MenuItem

import com.bodyweight.fitness.Constants
import com.bodyweight.fitness.adapter.ProgressPagerAdapter
import com.bodyweight.fitness.stream.DialogType
import com.bodyweight.fitness.repository.Repository

import org.joda.time.DateTime

import java.util.Locale

import com.bodyweight.fitness.R
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.stream.UiEvent

import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.activity_progress.*

class ProgressActivity : RxAppCompatActivity() {
    val primaryKeyRoutineId: String by lazy {
        intent.getStringExtra(Constants.primaryKeyRoutineId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_progress)

        val realm = Repository.realm
        val repositoryRoutine = realm.where(RepositoryRoutine::class.java)
                .equalTo("id", primaryKeyRoutineId)
                .findFirst()

        val progressPagerAdapter = ProgressPagerAdapter(repositoryRoutine)

        view_progress_pager.offscreenPageLimit = 4
        view_progress_pager.adapter = progressPagerAdapter

        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.title = DateTime(repositoryRoutine.startTime).toString("dd MMMM, YYYY", Locale.ENGLISH)
            it.elevation = 0f
            it.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        tablayout.setupWithViewPager(view_progress_pager)
        tablayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view_progress_pager.setCurrentItem(tab.position, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                if (tab.position != 0) {
                    progressPagerAdapter.onTabReselected(tab.position)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

//        UiEvent.dialogObservable
//                .bindToLifecycle(this)
//                .filter { it.dialogType == DialogType.LogWorkout }
//                .subscribe { dialog ->
//                    val bundle = Bundle()
//                    bundle.putString(Constants.primaryKeyRoutineId, primaryKeyRoutineId)
//                    bundle.putString(Constants.exerciseId, dialog.exerciseId)
//
//                    val logWorkoutDialog = LogWorkoutDialog()
//                    logWorkoutDialog.arguments = bundle
//
//                    logWorkoutDialog.show(supportFragmentManager, "dialog")
//                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
