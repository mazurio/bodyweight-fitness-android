package com.bodyweight.fitness.view.workout

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.AttributeSet

import com.bodyweight.fitness.*
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences
import com.bodyweight.fitness.view.AbstractPresenter
import com.bodyweight.fitness.view.AbstractView

import com.trello.rxlifecycle.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.view_timer.view.*

object RestTimerShared {
    var seconds = Preferences.restTimerDefaultSeconds
    var currentSeconds = seconds
    var startedLoggingSeconds = seconds
    var loggedSeconds = 0

    var isPlaying = false
    var restored = false

    var countDownTimer: CountDownTimer? = null
}

class RestTimerPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        RestTimerShared.countDownTimer?.cancel()

        if (RestTimerShared.restored) {
            RestTimerShared.restored = false

            restartTimer(RestTimerShared.currentSeconds, true, RestTimerShared.isPlaying)

            if (RestTimerShared.isPlaying) {
                startTimer()
            }
        } else {
            restartTimer(getSeconds(), false, false)
        }

        Stream.loggedSetRepsObservable
                .bindToLifecycle(view)
                .subscribe {
                    startTimer()
                }

        Stream.loggedSecondsObservable
                .bindToLifecycle(view)
                .subscribe {
                    startTimer()
                }
    }

    override fun saveView() {
        RestTimerShared.restored = true

        super.saveView()
    }

    fun startTimer() {
        if (Preferences.showRestTimer) {
            val section = RoutineStream.exercise.section!!

            if (section.sectionId == "section0") {
                if (Preferences.showRestTimerAfterWarmup) {
                    RestTimerShared.countDownTimer?.start()
                }
            } else if (section.sectionId == "section1") {
                if (Preferences.showRestTimerAfterBodylineDrills) {
                    RestTimerShared.countDownTimer?.start()
                }
            } else {
                if (RoutineStream.routine.routineId != "routine0") {
                    if (Preferences.showRestTimerAfterFlexibilityExercises) {
                        RestTimerShared.countDownTimer?.start()
                    }
                } else {
                    RestTimerShared.countDownTimer?.start()
                }
            }
        }
    }

    fun restartTimer(seconds: Int, restored: Boolean, isPlaying: Boolean) {
        val view = (mView as RestTimerView)

        RestTimerShared.countDownTimer?.cancel()

        RestTimerShared.isPlaying = isPlaying
        RestTimerShared.currentSeconds = seconds

        RestTimerShared.countDownTimer = buildCountDownTimer(seconds, restored)

        view.setMinutes(seconds.formatMinutes())
        view.setSeconds(seconds.formatSeconds())
    }

    fun playSound() {
        val view = (mView as RestTimerView)

        if (Preferences.playSoundWhenTimerStops()) {
            val mediaPlayer = MediaPlayer.create(view.context, R.raw.finished)

            mediaPlayer.isLooping = false
            mediaPlayer.start()
        }
    }

    fun onClickStartStopTimeButton() {
        restartTimer(seconds = getSeconds(), restored = false, isPlaying = false)

        Stream.setRestTimer()
    }

    fun buildCountDownTimer(seconds: Int, restored: Boolean): CountDownTimer? {
        val view = (mView as RestTimerView)

        if (restored) {
            RestTimerShared.loggedSeconds = RestTimerShared.startedLoggingSeconds
        } else {
            RestTimerShared.startedLoggingSeconds = seconds
            RestTimerShared.loggedSeconds = seconds
        }

        return object : CountDownTimer((seconds * 1000).toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timerSeconds = millisUntilFinished.toInt() / 1000

                RestTimerShared.isPlaying = true
                RestTimerShared.currentSeconds = timerSeconds

                view.setMinutes(timerSeconds.formatMinutes())
                view.setSeconds(timerSeconds.formatSeconds())
            }

            override fun onFinish() {
                restartTimer(getSeconds(), false, false)

                playSound()

                Stream.setRestTimer()
            }
        }
    }

    fun getSeconds(): Int {
        return Preferences.restTimerDefaultSeconds
    }
}

open class RestTimerView : AbstractView {
    override var presenter: AbstractPresenter = RestTimerPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val presenter = (presenter as RestTimerPresenter)

        stop_rest_timer_button.setOnClickListener { presenter.onClickStartStopTimeButton() }
    }

    fun setMinutes(text: String) {
        rest_timer_minutes.text = text
    }

    fun setSeconds(text: String) {
        rest_timer_seconds.text = text
    }
}