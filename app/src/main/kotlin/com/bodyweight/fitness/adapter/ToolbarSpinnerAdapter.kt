package com.bodyweight.fitness.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.bodyweight.fitness.R
import com.bodyweight.fitness.stream.RoutineStream
import kotlin.properties.Delegates

data class SpinnerRoutine(val id: Int, val name: String)

class ToolbarSpinnerAdapter : BaseAdapter() {
    val routines: List<SpinnerRoutine>

    init {
        if (RoutineStream.routine.routineId == "routine0") {
            routines = listOf(
                    SpinnerRoutine(0, "Recommended Routine"),
                    SpinnerRoutine(1, "Molding Mobility"))
        } else {
            routines = listOf(
                    SpinnerRoutine(1, "Molding Mobility"),
                    SpinnerRoutine(0, "Recommended Routine"))
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
        val textView = view!!.findViewById(android.R.id.text1) as TextView
        textView.text = getTitle(position)
        return view
    }

    private fun getTitle(position: Int): String {
        return routines[position].name
    }
}