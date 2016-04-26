package com.bodyweight.fitness.ui

import android.test.ActivityInstrumentationTestCase2

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

    fun testMainActivity() {
        Spoon.screenshot(solo!!.currentActivity, "start")
        Spoon.screenshot(solo!!.currentActivity, "end")
    }
}