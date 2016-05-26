package com.bodyweight.fitness.view

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.AttributeSet

import com.bodyweight.fitness.R
import com.bodyweight.fitness.formatMinutes
import com.bodyweight.fitness.formatSeconds
import com.bodyweight.fitness.stream.RoutineStream
import com.bodyweight.fitness.stream.Stream
import com.bodyweight.fitness.utils.Preferences

import com.trello.rxlifecycle.kotlin.bindToLifecycle

import kotlinx.android.synthetic.main.view_timer.view.*

object RestTimerShared {
    var currentSeconds = 60

    var isPlaying = false
    var restored = false

    var countDownTimer: CountDownTimer? = null
}

class RestTimerViewPresenter : AbstractPresenter() {
    override fun bindView(view: AbstractView) {
        super.bindView(view)

        Stream.drawerObservable()
                .bindToLifecycle(view)
                .filter { !it.equals(R.id.action_menu_workout) }
                .subscribe {
                    restartTimer(getSeconds(), false)
                }

        RoutineStream.exerciseObservable()
                .bindToLifecycle(view)
                .subscribe {
                    RestTimerShared.countDownTimer?.cancel()

                    if (RestTimerShared.restored) {
                        RestTimerShared.restored = false

                        restartTimer(RestTimerShared.currentSeconds, RestTimerShared.isPlaying)

                        if (RestTimerShared.isPlaying) {
                            startTimer()
                        }
                    } else {
                        restartTimer(getSeconds(), false)
                    }
                }
    }

    override fun saveView() {
        RestTimerShared.restored = true

        super.saveView()
    }

    fun startTimer() {
        RestTimerShared.countDownTimer?.start()
    }

    fun restartTimer(seconds: Int, isPlaying: Boolean) {
        val view = (mView as RestTimerView)

        RestTimerShared.countDownTimer?.cancel()

        RestTimerShared.isPlaying = isPlaying
        RestTimerShared.currentSeconds = seconds

        RestTimerShared.countDownTimer = buildCountDownTimer(seconds)

        view.setMinutes(seconds.formatMinutes())
        view.setSeconds(seconds.formatSeconds())

        view.showPaused()
    }

    fun playSound() {
        val view = (mView as RestTimerView)

        if (Preferences.playSoundWhenTimerStops()) {
            val mediaPlayer = MediaPlayer.create(view.context, R.raw.finished)

            mediaPlayer.isLooping = false
            mediaPlayer.start()
        }
    }

    fun onClickStartRestartTimeButton() {
        if (RestTimerShared.isPlaying) {
            restartTimer(getSeconds(), false)
        } else {
            restartTimer(getSeconds(), false)

            startTimer()
        }
    }

    fun buildCountDownTimer(seconds: Int): CountDownTimer? {
        val view = (mView as RestTimerView)

        return object : CountDownTimer((seconds * 1000).toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val timerSeconds = millisUntilFinished.toInt() / 1000

                RestTimerShared.isPlaying = true
                RestTimerShared.currentSeconds = timerSeconds

                view.setMinutes(timerSeconds.formatMinutes())
                view.setSeconds(timerSeconds.formatSeconds())

                view.showPlaying()
            }

            override fun onFinish() {
                restartTimer(getSeconds(), false)

                playSound()
            }
        }
    }

    fun getSeconds(): Int {
        return 60
//        return (Preferences.getTimerValueForExercise(RoutineStream.exercise.exerciseId, 60 * 1000) / 1000).toInt()
    }

}

open class RestTimerView : AbstractView {
    override var presenter: AbstractPresenter = RestTimerViewPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

        val presenter = (presenter as RestTimerViewPresenter)

        rest_start_restart_timer_button.setOnClickListener {
            presenter.onClickStartRestartTimeButton()
        }
    }

    fun setMinutes(text: String) {
        rest_timer_minutes.text = text
    }

    fun setSeconds(text: String) {
        rest_timer_seconds.text = text
    }

    fun showPlaying() {
        rest_start_restart_timer_button.setImageResource(R.drawable.action_refresh)
    }

    fun showPaused() {
        rest_start_restart_timer_button.setImageResource(R.drawable.action_play)
    }
}