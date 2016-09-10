package com.bodyweight.fitness.ui

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem

import com.bodyweight.fitness.R
import com.bodyweight.fitness.adapter.DashboardTreeAdapter
import com.bodyweight.fitness.stream.RoutineStream
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : RxAppCompatActivity() {
    val dashboardTreeAdapter: DashboardTreeAdapter by lazy {
        val routine = RoutineStream.routine
        val exercise = RoutineStream.exercise

        DashboardTreeAdapter(routine, exercise)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.title = ""
            it.subtitle = ""
            it.elevation = 0f
            it.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        dashboardTreeAdapter.asObservable().bindToLifecycle(this).subscribe {
            RoutineStream.exercise = it

            supportFinishAfterTransition()
        }

        view_dashboard_list.layoutManager = LinearLayoutManager(this)
        view_dashboard_list.adapter = dashboardTreeAdapter
    }

    override fun onResume() {
        super.onResume()

        view_dashboard_list.scrollToPosition(dashboardTreeAdapter.scrollPosition)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
