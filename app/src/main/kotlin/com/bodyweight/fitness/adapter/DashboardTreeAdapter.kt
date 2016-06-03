package com.bodyweight.fitness.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.bodyweight.fitness.R
import com.bodyweight.fitness.inflate
import com.bodyweight.fitness.model.*

import kotlinx.android.synthetic.main.view_dashboard_category.view.*
import kotlinx.android.synthetic.main.view_dashboard_double_item.view.*
import kotlinx.android.synthetic.main.view_dashboard_section.view.*
import kotlinx.android.synthetic.main.view_dashboard_single_item.view.*

import rx.Observable
import rx.subjects.PublishSubject

import java.util.HashMap
import java.util.HashSet

data class Tuple(val left: LinkedRoutine? = null, val right: LinkedRoutine? = null)

class DashboardTreeAdapter(private val routine: Routine, currentExercise: Exercise) : RecyclerView.Adapter<DashboardAbstractPresenter>() {
    private val dashboardTree = HashMap<Int, Tuple>()
    private val exerciseSubject = PublishSubject.create<Exercise>()

    var scrollPosition = 0
        private set

    init {
        var index = 0
        var skip = false

        val categorySet = HashSet<Category>()
        val set = HashSet<Section>()

        var firstInSection: Boolean

        for (exercise in routine.linkedExercises) {
            if (skip) {
                skip = false
            } else {
                val category = exercise.category
                if (!categorySet.contains(category)) {
                    categorySet.add(category!!)

                    dashboardTree.put(index, Tuple(category))

                    index++
                }

                val section = exercise.section
                if (!set.contains(section)) {
                    set.add(section!!)

                    dashboardTree.put(index, Tuple(section))

                    if (section.exercises.contains(currentExercise)) {
                        scrollPosition = index
                    }

                    firstInSection = true

                    index++
                } else {
                    firstInSection = false
                }

                if (exercise.section!!.sectionMode == SectionMode.All
                        && exercise.next != null
                        && exercise!!.next!!.section == exercise.section
                        && !firstInSection) {
                    dashboardTree.put(index, Tuple(exercise, exercise.next))

                    skip = true
                } else {
                    dashboardTree.put(index, Tuple(exercise))
                }

                index++
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardAbstractPresenter {
        when (viewType) {
            1 -> {
                val view = parent.inflate(R.layout.view_dashboard_section)
                return DashboardSectionPresenter(view)
            }

            2 -> {
                val view = parent.inflate(R.layout.view_dashboard_double_item)
                return DashboardDoubleItemPresenter(view)
            }

            3 -> {
                val view = parent.inflate(R.layout.view_dashboard_category)
                return DashboardCategoryPresenter(view)
            }

            else -> {
                val view = parent.inflate(R.layout.view_dashboard_single_item)
                return DashboardSingleItemPresenter(view)
            }
        }
    }

    override fun onBindViewHolder(holder: DashboardAbstractPresenter, position: Int) {
        holder.onBindView(exerciseSubject, dashboardTree[position]!!)
    }

    override fun getItemCount(): Int {
        return dashboardTree.size
    }

    override fun getItemViewType(position: Int): Int {
        val tuple = dashboardTree[position] as Tuple

        if (tuple.left?.type == RoutineType.Section) {
            return 1
        }

        if (tuple.left?.type == RoutineType.Category) {
            return 3
        }

        if (tuple.right != null) {
            return 2
        }

        return 0
    }

    fun asObservable(): Observable<Exercise> {
        return exerciseSubject.asObservable()
    }
}

abstract class DashboardAbstractPresenter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBindView(exerciseSubject: PublishSubject<Exercise>, tuple: Tuple)
}

class DashboardCategoryPresenter(itemView: View) : DashboardAbstractPresenter(itemView) {
    override fun onBindView(exerciseSubject: PublishSubject<Exercise>, tuple: Tuple) {
        val category = tuple.left as Category

        itemView.category_title.text = category.title
    }
}

class DashboardSectionPresenter(itemView: View) : DashboardAbstractPresenter(itemView) {
    override fun onBindView(exerciseSubject: PublishSubject<Exercise>, tuple: Tuple) {
        val section = tuple.left as Section

        if (section.sectionMode == SectionMode.All) {
            itemView.section_title.text = section.title
        } else {
            itemView.section_title.text = section.title
        }

        itemView.section_description.text = section.description
    }
}

class DashboardSingleItemPresenter(itemView: View) : DashboardAbstractPresenter(itemView) {
    override fun onBindView(exerciseSubject: PublishSubject<Exercise>, tuple: Tuple) {
        val exercise = tuple.left as Exercise

        if (exercise.isTimedSet) {
            itemView.exercise_button.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.dashboard_circle_timed))
        } else {
            itemView.exercise_button.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.dashboard_circle_weighted))
        }

        if (exercise.section!!.sectionMode == SectionMode.Levels) {
            itemView.exercise_title.text = exercise.title

            itemView.exercise_level.text = String.format("%s out of %s", exercise.level, exercise.section!!.exercises.size)
            itemView.exercise_level.visibility = View.VISIBLE
        } else {
            itemView.exercise_title.text = exercise.title
            itemView.exercise_level.visibility = View.GONE
        }

        itemView.exercise_button.setOnClickListener {
            exerciseSubject.onNext(exercise)
        }
    }
}

class DashboardDoubleItemPresenter(itemView: View) : DashboardAbstractPresenter(itemView) {
    override fun onBindView(exerciseSubject: PublishSubject<Exercise>, tuple: Tuple) {
        val leftExercise = tuple.left as Exercise
        val rightExercise = tuple.right as Exercise

        if (leftExercise.isTimedSet) {
            itemView.left_exercise_button.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.dashboard_circle_timed))
        } else {
            itemView.left_exercise_button.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.dashboard_circle_weighted))
        }

        if (rightExercise.isTimedSet) {
            itemView.right_exercise_button.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.dashboard_circle_timed))
        } else {
            itemView.right_exercise_button.setBackgroundDrawable(itemView.context.resources.getDrawable(R.drawable.dashboard_circle_weighted))
        }

        itemView.left_exercise_title.text = leftExercise.title
        itemView.right_exercise_title.text = rightExercise.title

        itemView.left_exercise_button.setOnClickListener {
            exerciseSubject.onNext(leftExercise)
        }

        itemView.right_exercise_button.setOnClickListener {
            exerciseSubject.onNext(rightExercise)
        }
    }
}