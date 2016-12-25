package com.bodyweight.fitness.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.bodyweight.fitness.R
import com.bodyweight.fitness.stream.RoutineStream

data class SpinnerRoutine(val id: Int, val title: String, val subtitle: String)

class ToolbarSpinnerAdapter : BaseAdapter() {
    val routines: List<SpinnerRoutine>

    init {
        val routineId = RoutineStream.routine.routineId

        if (routineId == "routine0") {
            routines = listOf(
                    SpinnerRoutine(0, "Bodyweight Fitness", "Recommended Routine"),
                    SpinnerRoutine(1, "Starting Stretching", "Flexibility Routine"),
                    SpinnerRoutine(2, "Molding Mobility", "Flexibility Routine")
            )
        } else if (routineId == "e73593f4-ee17-4b9b-912a-87fa3625f63d") {
            routines = listOf(
                    SpinnerRoutine(2, "Molding Mobility", "Flexibility Routine"),
                    SpinnerRoutine(0, "Bodyweight Fitness", "Recommended Routine"),
                    SpinnerRoutine(1, "Starting Stretching", "Flexibility Routine")
            )
        } else if (routineId == "d8a722a0-fae2-4e7e-a751-430348c659fe") {
            routines = listOf(
                    SpinnerRoutine(1, "Starting Stretching", "Flexibility Routine"),
                    SpinnerRoutine(0, "Bodyweight Fitness", "Recommended Routine"),
                    SpinnerRoutine(2, "Molding Mobility", "Flexibility Routine")
            )
        } else {
            routines = listOf(
                    SpinnerRoutine(0, "Bodyweight Fitness", "Recommended Routine"),
                    SpinnerRoutine(1, "Starting Stretching", "Flexibility Routine"),
                    SpinnerRoutine(2, "Molding Mobility", "Flexibility Routine")
            )
        }
    }

    override fun getCount(): Int {
        return routines.size
    }

    override fun getItem(position: Int): Any {
        return routines[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        if (view == null || view.tag.toString() != "DROPDOWN") {

            view = LayoutInflater.from(parent.context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false)
            view.tag = "DROPDOWN"
        }

        val textView = view!!.findViewById(android.R.id.text1) as TextView
        textView.text = getTitle(position)

        return view
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view

        if (view == null || view.tag.toString() != "NON_DROPDOWN") {
            view = LayoutInflater.from(parent.context).inflate(R.layout.toolbar_spinner_item_actionbar, parent, false)
            view.tag = "NON_DROPDOWN"
        }

        return view!!
    }

    private fun getTitle(position: Int): String {
        return routines[position].title
    }
}