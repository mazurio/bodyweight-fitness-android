package com.bodyweight.fitness.adapter

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.bodyweight.fitness.R
import com.bodyweight.fitness.inflate
import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.view.progress.ProgressGeneralView
import com.bodyweight.fitness.view.progress.ProgressGeneralViewPresenter

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

                if (RepositoryExercise.isCompleted(repositoryExercise)) {
                    currentExerciseIndex++
                }
            }
        }
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val viewPager = parent as ViewPager

        if (position == 0) {
            val view: ProgressGeneralView = parent.inflate(R.layout.activity_progress_general) as ProgressGeneralView
            val presenter: ProgressGeneralViewPresenter = view.presenter as ProgressGeneralViewPresenter

            presenter.repositoryRoutine = repositoryRoutine

            view.updateView()

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

    private fun createRecyclerView(view: View, position: Int) {
        val repositoryCategory = repositoryRoutine.categories[position - 1]

        view.recycler_view.layoutManager = LinearLayoutManager(view.context)
        view.recycler_view.adapter = ProgressListAdapter(repositoryCategory)

        viewWeakHashMap.put(position, view.recycler_view)
    }
}
