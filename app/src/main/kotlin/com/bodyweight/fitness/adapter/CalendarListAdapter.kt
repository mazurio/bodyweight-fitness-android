package com.bodyweight.fitness.adapter

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bodyweight.fitness.*

import com.bodyweight.fitness.model.repository.RepositoryRoutine
import com.bodyweight.fitness.stream.RepositoryStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.ui.ProgressActivity
import com.bodyweight.fitness.utils.PreferenceUtils

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder

import io.realm.RealmResults
import kotlinx.android.synthetic.main.view_calendar_card.view.*

class CalendarListAdapter : RecyclerView.Adapter<CalendarRoutinePresenter>() {
    private var mResults: RealmResults<RepositoryRoutine>? = null

    fun setItems(results: RealmResults<RepositoryRoutine>) {
        mResults = results

        notifyDataSetChanged()
    }

    fun removeItems() {
        mResults = null

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarRoutinePresenter {
        val view = parent.inflate(R.layout.view_calendar_card)

        return CalendarRoutinePresenter(view)
    }

    override fun onBindViewHolder(presenter: CalendarRoutinePresenter, position: Int) {
        mResults?.let {
            presenter.calendarListAdapter = this
            presenter.onBindView(it[position])
        }
    }

    override fun getItemCount(): Int = mResults?.size ?: 0
    override fun getItemViewType(position: Int): Int = 0
}

class CalendarRoutinePresenter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var calendarListAdapter: CalendarListAdapter? = null

    fun onBindView(repositoryRoutine: RepositoryRoutine) {
        itemView.view_calendar_routine_title.text = repositoryRoutine.title
        itemView.view_calendar_routine_subtitle.text = repositoryRoutine.subtitle

        itemView.view_calendar_card_view_button.setOnClickListener {
            val intent = Intent(it.context, ProgressActivity::class.java)
            intent.putExtra(Constants.PRIMARY_KEY_ROUTINE_ID, repositoryRoutine.id)

            it.context.startActivity(intent)
        }

        itemView.view_calendar_card_export_button.setOnClickListener {
            val intent = Intent(it.context, ProgressActivity::class.java)
            intent.putExtra(Constants.PRIMARY_KEY_ROUTINE_ID, repositoryRoutine.id)

            it.context.startActivity(intent)
        }

        itemView.view_calendar_card_export_button.setOnClickListener {
            val title = exportTitle(repositoryRoutine)
            val content = exportHTML(repositoryRoutine)

            val sendIntent = Intent()

            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TITLE, title)
            sendIntent.putExtra(Intent.EXTRA_TEXT, content)
            sendIntent.type = "text/plain"

            it.context.startActivity(sendIntent)
        }

        itemView.view_calendar_card_remove_button.setOnClickListener {
            AlertDialog.Builder(it.context)
                    .setTitle("Remove Logged Workout?")
                    .setPositiveButton("Ok") { dialog, which ->
                        val realm = RepositoryStream.getInstance().realm

                        realm.executeTransaction {
                            repositoryRoutine.deleteFromRealm()
                        }

                        calendarListAdapter?.removeItems()

                        Stream.setRepository()
                    }
                    .setNegativeButton("Cancel") { dialog, which ->  }
                    .show()
        }
    }

    private fun exportTitle(repositoryRoutine: RepositoryRoutine): String {
        var title = ""

        title += String.format("%s - %s - %s.",
                repositoryRoutine.title,
                repositoryRoutine.subtitle,
                DateTime(repositoryRoutine.startTime).toString("EEEE, d MMMM YYYY - HH:mm"))

        return title
    }

    private fun exportHTML(repositoryRoutine: RepositoryRoutine): String {
        var content = ""

        content += "Hello, The following is your workout in Text/HTML format."

        content += String.format("\n\nWorkout on %s.", DateTime(repositoryRoutine.startTime).toString("EEEE, d MMMM YYYY - HH:mm"))
        content += String.format("\nLast Updated at %s.", DateTime(repositoryRoutine.lastUpdatedTime).toString("HH:mm"))
        content += String.format("\nWorkout length: %s.", workoutLength(repositoryRoutine))
        content += String.format("\n\n%s - %s", repositoryRoutine.title, repositoryRoutine.subtitle)

        for (exercise in repositoryRoutine.exercises) {
            if (exercise.isVisible) {
                content += String.format("\n\n%s", exercise.title)

                val weightUnit = PreferenceUtils.getInstance().weightMeasurementUnit.toString()

                var index = 0
                for (set in exercise.sets) {
                    index++

                    content += String.format("\nSet %s", index)

                    if (set.isTimed) {
                        content += String.format("\nMinutes: %s", set.seconds.formatMinutes(false))
                        content += String.format("\nSeconds: %s", set.seconds.formatSeconds(false))
                    } else {
                        content += String.format("\nReps: %s", set.reps)
                        content += String.format("\nWeight: %s %s", set.weight, weightUnit)
                    }
                }
            }
        }

        return content
    }

    fun workoutLength(repositoryRoutine: RepositoryRoutine): String {
        val startTime = DateTime(repositoryRoutine.startTime)
        val lastUpdatedTime = DateTime(repositoryRoutine.lastUpdatedTime)

        val duration = Duration(startTime, lastUpdatedTime)

        if (duration.toStandardMinutes().minutes < 10) {
            return "--"
        } else {
            return PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix("h ")
                    .appendMinutes()
                    .appendSuffix("m")
                    .toFormatter()
                    .print(duration.toPeriod())
        }
    }
}
