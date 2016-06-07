package com.bodyweight.fitness.view

import android.content.Context
import android.content.Intent
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.bodyweight.fitness.Constants

import com.bodyweight.fitness.R
import com.bodyweight.fitness.adapter.CalendarPagerAdapter
import com.bodyweight.fitness.adapter.CalendarListAdapter
import com.bodyweight.fitness.isRoutineLoggedWithResults
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.Stream

import com.trello.rxlifecycle.kotlin.bindToLifecycle

import io.realm.RealmResults
import io.realm.Sort

import kotlinx.android.synthetic.main.view_calendar.view.*

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers

import java.io.File
import java.io.FileOutputStream

class CalendarPresenter : AbstractPresenter() {
    @Transient
    val calendarAdapter = CalendarPagerAdapter()

    @Transient
    val calendarListAdapter = CalendarListAdapter()

    override fun bindView(view: AbstractView) {
        super.bindView(view)

        val view = (view as CalendarView)

        view.view_calendar_pager.adapter = calendarAdapter
        view.view_calendar_list.adapter = calendarListAdapter

        view.scrollToDefaultItem()

        Stream.menuObservable
                .bindToLifecycle(view)
                .filter { it == R.id.action_today }
                .subscribe {
                    view.scrollToDefaultItem()
                }

        Stream.menuObservable
                .bindToLifecycle(view)
                .filter { it == R.id.action_export }
                .subscribe {
                    Repository.realm.where(RepositoryRoutine::class.java)
                            .findAllAsync()
                            .sort("startTime", Sort.DESCENDING)
                            .asObservable()
                            .filter { it.isLoaded }
                            .observeOn(AndroidSchedulers.mainThread())
                            .bindToLifecycle(getView())
                            .subscribe(object: Subscriber<RealmResults<RepositoryRoutine>>(){
                                override fun onCompleted() {}

                                override fun onError(e: Throwable) {
                                    Toast.makeText(getView().context, "Error: Unable to export workout log", Toast.LENGTH_SHORT).show()
                                }

                                override fun onNext(it: RealmResults<RepositoryRoutine>) {
                                    val context = getView().context

                                    val path = File(context.filesDir, "csv");
                                    val file = File(path, "LoggedWorkouts.csv").apply {
                                        if (parentFile.mkdirs()) {
                                            createNewFile()
                                        }
                                    }

                                    var content = ""

                                    for (repositoryRoutine: RepositoryRoutine in it) {
                                        content += RepositoryRoutine.toCSV(repositoryRoutine)
                                    }

                                    FileOutputStream(file).apply {
                                        write(content.toByteArray())
                                        flush()
                                        close()
                                    }

                                    context.startActivity(Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/csv"
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                                        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, Constants.fileProvider, file))
                                    })
                                }
                            })
                }

        Stream.calendarDayObservable()
                .bindToLifecycle(view)
                .subscribe {
                    val results = it.getDate().isRoutineLoggedWithResults()

                    if (results.isNotEmpty()) {
                        calendarListAdapter.setItems(results)

                        view.showCardView()
                    } else {
                        view.hideCardView()
                    }
                }
    }

    fun onPageSelected(position: Int) {
        Stream.setCalendarPage(position)
    }
}

open class CalendarView : AbstractView {
    override var presenter: AbstractPresenter = CalendarPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateView() {
        view_calendar_list.layoutManager = LinearLayoutManager(context)

        view_calendar_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val presenter = (presenter as CalendarPresenter)

                presenter.onPageSelected(position)
            }
        })
    }

    fun hideCardView() {
        view_calendar_list.visibility = View.GONE
        view_calendar_message.visibility = View.VISIBLE
    }

    fun showCardView() {
        view_calendar_list.visibility = View.VISIBLE
        view_calendar_message.visibility = View.GONE
    }

    fun scrollToDefaultItem() {
        view_calendar_pager.setCurrentItem(CalendarPagerAdapter.DEFAULT_POSITION, false)
    }
}