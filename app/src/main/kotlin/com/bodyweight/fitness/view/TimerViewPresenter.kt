package com.bodyweight.fitness.view

import android.app.TimePickerDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.AttributeSet

import com.bodyweight.fitness.*
import com.bodyweight.fitness.extension.*
import com.bodyweight.fitness.model.RepositoryExercise
import com.bodyweight.fitness.model.RepositorySet
import com.bodyweight.fitness.repository.Repository
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_timer.view.*
import org.joda.time.DateTime
import java.util.*

object TimerShared {
    var mSeconds = 60
    var mCurrentSeconds = mSeconds
    var mStartedLoggingSeconds = mSeconds
    var mLoggedSeconds = 0

    var mPlaying = false
    var mRestored = false

    var mCountDownTimer: CountDownTimer? = null
}

class TimerPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable
                .bindToLifecycle(view)
                .subscribe {
                    pauseTimer()
                }

        getExerciseObservable()
                .bindToLifecycle(view)
                .doOnSubscribe { debug(this.javaClass.simpleName + " = doOnSubscribe") }
                .doOnUnsubscribe { debug(this.javaClass.simpleName + " = doOnUnsubscribe") }
                .subscribe {
                    TimerShared.mCountDownTimer?.cancel()

                    if (TimerShared.mRestored) {
                        TimerShared.mRestored = false

                        restartTimer(TimerShared.mCurrentSeconds, true, TimerShared.mPlaying)

                        if (TimerShared.mPlaying) {
                            startTimer()
                        }
                    } else {
                        restartTimer(getSeconds(), false, false)
                    }
                }
    }

    override fun saveView() {
        TimerShared.mRestored = true

        super.saveView()
    }

    fun increaseTimer(extraSeconds: Int) {
        val view = (mView as TimerView)

        if (TimerShared.mPlaying) {
            TimerShared.mCountDownTimer?.cancel()

            TimerShared.mCurrentSeconds += extraSeconds
            TimerShared.mCountDownTimer = buildCountDownTimer(TimerShared.mCurrentSeconds, false, true)

            view.setMinutes(TimerShared.mCurrentSeconds.formatMinutes())
            view.setSeconds(TimerShared.mCurrentSeconds.formatSeconds())

            TimerShared.mCountDownTimer?.start()
        } else {
            TimerShared.mCurrentSeconds += extraSeconds
            TimerShared.mCountDownTimer = buildCountDownTimer(TimerShared.mCurrentSeconds, false, true)

            view.setMinutes(TimerShared.mCurrentSeconds.formatMinutes())
            view.setSeconds(TimerShared.mCurrentSeconds.formatSeconds())
        }
    }

    fun pauseTimer() {
        val view = (mView as TimerView)

        TimerShared.mCountDownTimer?.cancel()

        TimerShared.mPlaying = false
        TimerShared.mCountDownTimer = buildCountDownTimer(TimerShared.mCurrentSeconds, false, false)

        view.setMinutes(TimerShared.mCurrentSeconds.formatMinutes())
        view.setSeconds(TimerShared.mCurrentSeconds.formatSeconds())

        view.showPaused()
    }

    fun startTimer() {
        TimerShared.mCountDownTimer?.start()
    }

    fun restartTimer(seconds: Int, restored: Boolean, isPlaying: Boolean) {
        val view = (mView as TimerView)

        TimerShared.mCountDownTimer?.cancel()

        TimerShared.mPlaying = isPlaying
        TimerShared.mCurrentSeconds = seconds

        TimerShared.mCountDownTimer = buildCountDownTimer(seconds, restored, false)

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
            TimerShared.mCurrentSeconds = seconds + minutes * 60

            if (TimerShared.mCurrentSeconds < 10) {
                TimerShared.mCurrentSeconds = 10
            }

            restartTimer(TimerShared.mCurrentSeconds, false, false)

            TimerShared.mSeconds = TimerShared.mCurrentSeconds

            val save = (TimerShared.mSeconds * 1000).toLong()

            Preferences.setTimerValue(getCurrentExercise().exerciseId, save)
        }, TimerShared.mCurrentSeconds.formatMinutesAsNumber(), TimerShared.mCurrentSeconds.formatSecondsAsNumber(), true)

        timePickerDialog.show()
    }

    fun onClickIncreaseTimeButton() {
        increaseTimer(5)
    }

    fun onClickStartStopTimeButton() {
        if (TimerShared.mPlaying) {
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
            TimerShared.mLoggedSeconds += 5
        } else {
            if (restored) {
                TimerShared.mLoggedSeconds = TimerShared.mStartedLoggingSeconds
            } else {
                TimerShared.mStartedLoggingSeconds = seconds
                TimerShared.mLoggedSeconds = seconds
            }
        }

        return object : CountDownTimer((seconds * 1000).toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timerSeconds = millisUntilFinished.toInt() / 1000

                TimerShared.mPlaying = true
                TimerShared.mCurrentSeconds = timerSeconds

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
        return (Preferences.getTimerValueForExercise(getCurrentExercise().exerciseId, 60 * 1000) / 1000).toInt()
    }

    fun logTime() {
        if (Preferences.automaticallyLogWorkoutTime() && getCurrentExercise().isTimedSet) {
            val loggedSeconds = TimerShared.mLoggedSeconds - TimerShared.mCurrentSeconds

            if (loggedSeconds > 0) {
                if (logIntoRealm(loggedSeconds)) {
                    Stream.setLoggedSeconds(loggedSeconds)
                }
            }
        }
    }

    private fun logIntoRealm(logSeconds: Int): Boolean {
        // getRepositoryRoutineForToday method - begins realm transaction.
        val repositoryRoutine = Repository.repositoryRoutineForToday
        var mRepositoryExercise: RepositoryExercise? = null

        val realm = Repository.realm
        val exercise = RoutineStream.exercise

        realm.beginTransaction()
        for (repositoryExercise in repositoryRoutine.exercises) {
            if (repositoryExercise.title == exercise.title) {
                mRepositoryExercise = repositoryExercise

                break
            }
        }

        if (mRepositoryExercise != null) {
            // if there is already a set which is timed and has 0 seconds then overwrite the values.
            // otherwise create new one.

            val numberOfSets = mRepositoryExercise.sets.size

            if (numberOfSets >= Constants.maximumNumberOfSets) {
                realm.cancelTransaction()

                return false
            }

            if (numberOfSets == 1 && mRepositoryExercise.sets[0].isTimed && mRepositoryExercise.sets[0].seconds == 0) {
                val firstSet = mRepositoryExercise.sets[0]

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

                repositorySet.exercise = mRepositoryExercise

                mRepositoryExercise.sets.add(repositorySet)
            }

            // TODO: Unit test the fact that lastUpdatedTime is changed when logging sets from main UI.
            repositoryRoutine.lastUpdatedTime = DateTime().toDate()

            realm.copyToRealmOrUpdate(repositoryRoutine)
            realm.commitTransaction()

            return true
        } else {
            realm.cancelTransaction()

            return false
        }
    }
}

open class TimerView : AbstractView {
    override var mPresenter: AbstractPresenter = TimerPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val presenter = (mPresenter as TimerPresenter)

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