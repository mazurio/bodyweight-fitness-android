package com.bodyweight.fitness

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.test.ActivityInstrumentationTestCase2

import com.bodyweight.fitness.ui.MainActivity

class MainActivityTest : ActivityInstrumentationTestCase2<MainActivity>(MainActivity::class.java) {
    override fun setUp() {
        super.setUp()

        activity
    }

    fun testExerciseSetup() {
        onView(withId(R.id.toolbar_exercise_title))
                .check(matches(withText("Shoulder Rolls")))
        onView(withId(R.id.toolbar_section_title))
                .check(matches(withText("Dynamic Stretches")))
        onView(withId(R.id.toolbar_exercise_description))
                .check(matches(withText("1x(5-10)")))

        onView(withId(R.id.prev_exercise_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.next_exercise_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.next_exercise_button)).perform(click())

        onView(withId(R.id.toolbar_exercise_title))
                .check(matches(withText("Scapular Shrugs")))
        onView(withId(R.id.toolbar_section_title))
                .check(matches(withText("Dynamic Stretches")))
        onView(withId(R.id.toolbar_exercise_description))
                .check(matches(withText("1x(5-10)")))

        onView(withId(R.id.prev_exercise_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.next_exercise_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.next_exercise_button)).perform(click())
        onView(withId(R.id.next_exercise_button)).perform(click())
        onView(withId(R.id.next_exercise_button)).perform(click())
        onView(withId(R.id.next_exercise_button)).perform(click())
        onView(withId(R.id.next_exercise_button)).perform(click())

        onView(withId(R.id.toolbar_exercise_title))
                .check(matches(withText("Front and Side Leg Swings")))
        onView(withId(R.id.toolbar_section_title))
                .check(matches(withText("Dynamic Stretches")))
        onView(withId(R.id.toolbar_exercise_description))
                .check(matches(withText("1x(5-10)")))

        onView(withId(R.id.prev_exercise_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.next_exercise_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}