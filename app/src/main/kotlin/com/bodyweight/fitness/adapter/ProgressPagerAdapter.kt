package com.bodyweight.fitness.adapter

import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.hookedonplay.decoviewlib.charts.DecoDrawEffect
import com.hookedonplay.decoviewlib.charts.SeriesItem
import com.hookedonplay.decoviewlib.events.DecoEvent

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder

import com.bodyweight.fitness.R
import com.bodyweight.fitness.Exercise
import com.bodyweight.fitness.inflate
import com.bodyweight.fitness.model.RepositoryRoutine

import kotlinx.android.synthetic.main.activity_progress_info.view.*
import kotlinx.android.synthetic.main.activity_progress_page.view.*

import java.util.*

class ProgressPagerAdapter(private val repositoryRoutine: RepositoryRoutine) : PagerAdapter() {
    private var numberOfExercises: Int = 0
    private var currentExerciseIndex: Int = 0
    private val viewWeakHashMap = WeakHashMap<Int, RecyclerView>()

    init {
        for (repositoryExercise in repositoryRoutine.exercises) {
            if (repositoryExercise.isVisible) {
                numberOfExercises++

                if (Exercise.isCompleted(repositoryExercise)) {
                    currentExerciseIndex++
                }
            }
        }
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val viewPager = parent as ViewPager

        if (position == 0) {
            val view = parent.inflate(R.layout.activity_progress_info)

            createStartTimeEndTime(view)
            createDecoView(view)
            viewPager.addView(view)

            return view
        }

        val view = parent.inflate(R.layout.activity_progress_page)

        createRecyclerView(view, position)
        viewPager.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

        viewWeakHashMap.remove(position)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return repositoryRoutine.categories.size + 1
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "General"
            else -> return repositoryRoutine.categories[position - 1].title!!
        }
    }

    fun onTabReselected(tabPosition: Int) {
        viewWeakHashMap[tabPosition]?.smoothScrollToPosition(0)
    }

    private fun createStartTimeEndTime(view: View) {
        val startTime = DateTime(repositoryRoutine.startTime)
        val lastUpdatedTime = DateTime(repositoryRoutine.lastUpdatedTime)
        val duration = Duration(startTime, lastUpdatedTime)

        view.start_time_text.text = startTime.toString("HH:mm", Locale.ENGLISH)
        view.end_time_text.text = lastUpdatedTime.toString("HH:mm", Locale.ENGLISH)

        if (duration.toStandardMinutes().minutes < 10) {
            view.workout_length_text.text = "--"
        } else {
            view.workout_length_text.text = PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix("h ")
                    .appendMinutes()
                    .appendSuffix("m")
                    .toFormatter()
                    .print(duration.toPeriod())
        }

        if (currentExerciseIndex == numberOfExercises) {
            view.end_time_label.text = "End Time"
        } else {
            view.end_time_label.text = "Last Updated"
        }
    }

    private fun createRecyclerView(view: View, position: Int) {
        val repositoryCategory = repositoryRoutine.categories[position - 1]

        view.recycler_view.layoutManager = LinearLayoutManager(view.context)
        view.recycler_view.adapter = ProgressAdapter(repositoryCategory)

        viewWeakHashMap.put(position, view.recycler_view)
    }

    private fun createDecoView(view: View) {
        val backIndex = view.dynamicArcView.addSeries(SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0f, numberOfExercises.toFloat(), 0f)
                .setInitialVisibility(true)
                .build())

        val frontSeries = SeriesItem.Builder(Color.parseColor("#0B9394"))
                .setRange(0f, numberOfExercises.toFloat(), 0f)
                .setInitialVisibility(false)
                .build()

        frontSeries.addArcSeriesItemListener(object : SeriesItem.SeriesItemListener {
            override fun onSeriesItemAnimationProgress(percentComplete: Float, currentPosition: Float) {
                val percentFilled = (currentPosition - frontSeries.minValue) / (frontSeries.maxValue - frontSeries.minValue)

                view.textPercentage.text = String.format("%.0f%%", percentFilled * 100f)
            }

            override fun onSeriesItemDisplayProgress(percentComplete: Float) { }
        })

        frontSeries.addArcSeriesItemListener(object : SeriesItem.SeriesItemListener {
            override fun onSeriesItemAnimationProgress(percentComplete: Float, currentPosition: Float) {
                val remainingExercises = (frontSeries.maxValue - currentPosition).toInt()

                if (remainingExercises > 0) {
                    view.textRemaining.text = String.format("%d exercises to go", remainingExercises)
                } else {
                    view.textRemaining.text = "Congratulations!"
                }
            }

            override fun onSeriesItemDisplayProgress(percentComplete: Float) { }
        })

        val frontIndex = view.dynamicArcView.addSeries(frontSeries)

        view.dynamicArcView.executeReset()
        view.dynamicArcView.addEvent(DecoEvent.Builder(numberOfExercises.toFloat())
                .setIndex(backIndex)
                .setDuration(0)
                .setDelay(0)
                .build())

        view.dynamicArcView.addEvent(DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(frontIndex)
                .setDuration(0)
                .setDelay(0)
                .build())

        view.dynamicArcView.addEvent(DecoEvent.Builder(currentExerciseIndex.toFloat())
                .setIndex(frontIndex)
                .setDelay(0)
                .build())

        view.textPercentage.text = ""
        view.textRemaining.text = ""
    }
}
