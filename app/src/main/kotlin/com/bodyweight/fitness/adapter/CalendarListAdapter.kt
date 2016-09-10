package com.bodyweight.fitness.adapter

import android.content.Intent
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.bodyweight.fitness.*
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.ui.ProgressActivity

import io.realm.RealmResults

import kotlinx.android.synthetic.main.view_calendar_card.view.*

import java.io.File
import java.io.FileOutputStream

class CalendarListAdapter : RecyclerView.Adapter<CalendarRoutinePresenter>() {
    private var repositoryRoutineList: RealmResults<RepositoryRoutine>? = null

    fun setItems(results: RealmResults<RepositoryRoutine>) {
        repositoryRoutineList = results

        notifyDataSetChanged()
    }

    fun removeItem(repositoryRoutine: RepositoryRoutine) {
        repositoryRoutineList?.remove(repositoryRoutine)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarRoutinePresenter {
        val view = parent.inflate(R.layout.view_calendar_card)

        return CalendarRoutinePresenter(view)
    }

    override fun onBindViewHolder(presenter: CalendarRoutinePresenter, position: Int) {
        repositoryRoutineList?.let {
            presenter.calendarListAdapter = this
            presenter.onBindView(it[position])
        }
    }

    override fun getItemCount(): Int = repositoryRoutineList?.size ?: 0
    override fun getItemViewType(position: Int): Int = 0
}

class CalendarRoutinePresenter(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var calendarListAdapter: CalendarListAdapter? = null

    fun onBindView(repositoryRoutine: RepositoryRoutine) {
        itemView.view_calendar_routine_title.text = repositoryRoutine.title
        itemView.view_calendar_routine_subtitle.text = repositoryRoutine.subtitle

        val completionRate = RepositoryRoutine.getCompletionRate(repositoryRoutine)

        itemView.completion_rate_label.text = completionRate.label
        itemView.completion_rate_value.setLayoutWeight(calculateLayoutWeight(completionRate.percentage))

        itemView.view_calendar_card_view_button.setOnClickListener {
            val intent = Intent(it.context, ProgressActivity::class.java)
            intent.putExtra(Constants.primaryKeyRoutineId, repositoryRoutine.id)

            it.context.startActivity(intent)
        }

        itemView.view_calendar_card_export_button.setOnClickListener {
            val intent = Intent(it.context, ProgressActivity::class.java)
            intent.putExtra(Constants.primaryKeyRoutineId, repositoryRoutine.id)

            it.context.startActivity(intent)
        }

        itemView.view_calendar_card_export_button.setOnClickListener {
            val context = it.context

            try {
                val path = File(context.filesDir, "csv");
                val file = File(path, "LoggedWorkout.csv").apply {
                    if (parentFile.mkdirs()) {
                        createNewFile()
                    }
                }

                FileOutputStream(file).apply {
                    write(RepositoryRoutine.toCSV(repositoryRoutine).toByteArray())
                    flush()
                    close()
                }

                context.startActivity(Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    putExtra(Intent.EXTRA_TITLE, RepositoryRoutine.getTitleWithDate(repositoryRoutine))
                    putExtra(Intent.EXTRA_TEXT, RepositoryRoutine.toText(repositoryRoutine))
                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, Constants.fileProvider, file))
                })
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(context, "Error: Unable to export workout log", Toast.LENGTH_SHORT).show()
            }
        }

        itemView.view_calendar_card_remove_button.setOnClickListener {
            AlertDialog.Builder(it.context)
                    .setTitle("Remove Logged Workout?")
                    .setPositiveButton("Ok") { dialog, which ->
                        val realm = Repository.realm

                        realm.executeTransaction {
                            repositoryRoutine.deleteFromRealm()
                        }

                        Stream.setRepository()
                    }
                    .setNegativeButton("Cancel") { dialog, which ->  }
                    .show()
        }
    }
}
