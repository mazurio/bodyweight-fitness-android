package com.bodyweight.fitness.ui

import android.app.Activity
import android.test.ActivityInstrumentationTestCase2
import android.test.UiThreadTest
import com.bodyweight.fitness.R

import com.robotium.solo.Solo
import com.squareup.spoon.Spoon

class MainActivityTest : ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {
    private var solo: Solo? = null

    override fun setUp() {
        solo = Solo(instrumentation, activity)

        super.setUp()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        solo!!.finishOpenedActivities()

        super.tearDown()
    }

    @UiThreadTest
    fun testMainActivity() {
        Spoon.screenshot(currentActivity(), "start")

        val button = getSolo().getView(R.id.next_exercise_button)

        var index = 0
        while (button.isClickable) {
            Spoon.screenshot(currentActivity(), "exercise" + index)

            button.performClick()

            index++;
        }

        Spoon.screenshot(currentActivity(), "end")
    }

    fun getSolo(): Solo {
        return solo!!
    }

    fun currentActivity(): Activity {
        return getSolo().currentActivity
    }
}