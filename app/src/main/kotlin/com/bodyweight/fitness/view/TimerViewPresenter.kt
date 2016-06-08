package com.bodyweight.fitness.view

import android.app.TimePickerDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.AttributeSet

import com.bodyweight.fitness.*
import com.bodyweight.fitness.model.RepositoryRoutine
import com.bodyweight.fitness.model.RepositorySet
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_timer.view.*
import java.util.*

object TimerShared {
    var seconds = 60
    var currentSeconds = seconds
    var startedLoggingSeconds = seconds
    var loggedSeconds = 0

    var isPlaying = false
    var restored = false

    var countDownTimer: CountDownTimer? = null
}

class TimerPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .filter { !it.equals(R.id.action_menu_workout) }
                .subscribe {
                    pauseTimer()
                }

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    TimerShared.countDownTimer?.cancel()

                    if (TimerShared.restored) {
                        TimerShared.restored = false

                        restartTimer(TimerShared.currentSeconds, true, TimerShared.isPlaying)

                        if (TimerShared.isPlaying) {
                            startTimer()
                        }
                    } else {
                        restartTimer(getSeconds(), false, false)
                    }
                }
    }

    override fun saveView() {
        TimerShared.restored = true

        super.saveView()
    }

    fun increaseTimer(extraSeconds: Int) {
        val view = (mView as TimerView)

        if (TimerShared.isPlaying) {
            TimerShared.countDownTimer?.cancel()

            TimerShared.currentSeconds += extraSeconds
            TimerShared.countDownTimer = buildCountDownTimer(TimerShared.currentSeconds, false, true)

            view.setMinutes(TimerShared.currentSeconds.formatMinutes())
            view.setSeconds(TimerShared.currentSeconds.formatSeconds())

            TimerShared.countDownTimer?.start()
        } else {
            TimerShared.currentSeconds += extraSeconds
            TimerShared.countDownTimer = buildCountDownTimer(TimerShared.currentSeconds, false, true)

            view.setMinutes(TimerShared.currentSeconds.formatMinutes())
            view.setSeconds(TimerShared.currentSeconds.formatSeconds())
        }
    }

    fun pauseTimer() {
        val view = (mView as TimerView)

        TimerShared.countDownTimer?.cancel()

        TimerShared.isPlaying = false
        TimerShared.countDownTimer = buildCountDownTimer(TimerShared.currentSeconds, false, false)

        view.setMinutes(TimerShared.currentSeconds.formatMinutes())
        view.setSeconds(TimerShared.currentSeconds.formatSeconds())

        view.showPaused()
    }

    fun startTimer() {
        TimerShared.countDownTimer?.start()
    }

    fun restartTimer(seconds: Int, restored: Boolean, isPlaying: Boolean) {
        val view = (mView as TimerView)

        TimerShared.countDownTimer?.cancel()

        TimerShared.isPlaying = isPlaying
        TimerShared.currentSeconds = seconds

        TimerShared.countDownTimer = buildCountDownTimer(seconds, restored, false)

        view.setMinutes(seconds.formatMinutes())
        view.setSeconds(seconds.formatSeconds())

        view.showPaused()
    }

    fun playSound() {
        val view = (mView as TimerView)

        if (Preferences.playSoundWhenTimerStops()) {
            val mediaPlayer = MediaPlayer.create(view.context, R.raw.finished)

            mediaPlayer.isLooping = false
            mediaPlayer.start()
        }
    }

    fun onClickTimeLayout() {
        val view = (mView as TimerView)

        pauseTimer()

        val timePickerDialog = TimePickerDialog(view.context, { view, minutes, seconds ->
            TimerShared.currentSeconds = seconds + minutes * 60

            if (TimerShared.currentSeconds < 10) {
                TimerShared.currentSeconds = 10
            }

            restartTimer(TimerShared.currentSeconds, false, false)

            TimerShared.seconds = TimerShared.currentSeconds

            val save = (TimerShared.seconds * 1000).toLong()

            Preferences.setTimerValue(RoutineStream.exercise.exerciseId, save)
        }, TimerShared.currentSeconds.formatMinutesAsNumber(), TimerShared.currentSeconds.formatSecondsAsNumber(), true)

        timePickerDialog.show()
    }

    fun onClickIncreaseTimeButton() {
        increaseTimer(5)
    }

    fun onClickStartStopTimeButton() {
        if (TimerShared.isPlaying) {
            logTime()

            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun onClickRestartTimeButton() {
        logTime()

        restartTimer(getSeconds(), false, false)
    }

    fun buildCountDownTimer(seconds: Int, restored: Boolean, increaseTimer: Boolean): CountDownTimer? {
        val view = (mView as TimerView)

        if (increaseTimer) {
            TimerShared.loggedSeconds += 5
        } else {
            if (restored) {
                TimerShared.loggedSeconds = TimerShared.startedLoggingSeconds
            } else {
                TimerShared.startedLoggingSeconds = seconds
                TimerShared.loggedSeconds = seconds
            }
        }

        return object : CountDownTimer((seconds * 1000).toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timerSeconds = millisUntilFinished.toInt() / 1000

                TimerShared.isPlaying = true
                TimerShared.currentSeconds = timerSeconds

                view.setMinutes(timerSeconds.formatMinutes())
                view.setSeconds(timerSeconds.formatSeconds())

                view.showPlaying()
            }

            override fun onFinish() {
                logTime()

                restartTimer(getSeconds(), false, false)

                playSound()
            }
        }
    }

    fun getSeconds(): Int {
        return (Preferences.getTimerValueForExercise(RoutineStream.exercise.exerciseId, 60 * 1000) / 1000).toInt()
    }

    fun logTime() {
        if (Preferences.automaticallyLogWorkoutTime() && RoutineStream.exercise.isTimedSet) {
            val loggedSeconds = TimerShared.loggedSeconds - TimerShared.currentSeconds

            if (loggedSeconds > 0) {
                if (logIntoRealm(loggedSeconds)) {
                    Stream.setLoggedSeconds(loggedSeconds)
                }
            }
        }
    }

    private fun logIntoRealm(logSeconds: Int): Boolean {
        val realm = Repository.realm
        val repositoryRoutine = Repository.repositoryRoutineForToday

        var isLogged: Boolean = false

        realm.executeTransaction {
            repositoryRoutine.exercises
                    .filter { it.exerciseId == RoutineStream.exercise.exerciseId }
                    .firstOrNull()?.let {

                val numberOfSets = it.sets.size
                if (numberOfSets < Constants.maximumNumberOfSets) {
                    val firstSet = it.sets.first()

                    if (numberOfSets == 1 && firstSet.isTimed && firstSet.seconds == 0) {
                        if (firstSet.isTimed && firstSet.seconds == 0) {
                            firstSet.seconds = logSeconds
                        }
                    } else {
                        val repositorySet = realm.createObject(RepositorySet::class.java)

                        repositorySet.id = "Set-" + UUID.randomUUID().toString()
                        repositorySet.isTimed = true
                        repositorySet.seconds = logSeconds
                        repositorySet.weight = 0.0
                        repositorySet.reps = 0

                        repositorySet.exercise = it

                        it.sets.add(repositorySet)
                    }

                    RepositoryRoutine.setLastUpdatedTime(repositoryRoutine, isNestedTransaction = true)

                    isLogged = true
                }
            }
        }

        return isLogged
    }
}

open class TimerView : AbstractView {
    override var presenter: AbstractPresenter = TimerPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val presenter = (presenter as TimerPresenter)

        timer_layout.setOnClickListener { presenter.onClickTimeLayout() }
        increase_timer_button.setOnClickListener { presenter.onClickIncreaseTimeButton() }
        start_stop_timer_button.setOnClickListener { presenter.onClickStartStopTimeButton() }
        restart_timer_button.setOnClickListener { presenter.onClickRestartTimeButton() }
    }

    override fun onCreateView() { }

    fun setMinutes(text: String) {
        timer_minutes.text = text
    }

    fun setSeconds(text: String) {
        timer_seconds.text = text
    }

    fun showPlaying() {
        start_stop_timer_button.setImageResource(R.drawable.action_pause)
    }

    fun showPaused() {
        start_stop_timer_button.setImageResource(R.drawable.action_play)
    }
}