package com.nhaarman.bravo.samples.hellostaterestoration

import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withHint
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class HelloStateRestorationTest {

    @Rule @JvmField val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun navigatingThroughScenes() {
        onView(withText("0")).check(matches(isDisplayed()))
        onView(withText("Next")).perform(click())
        onView(withText("1")).check(matches(isDisplayed()))
        onView(withText("Next")).perform(click())
        onView(withText("2")).check(matches(isDisplayed()))
        onView(withText("Next")).perform(click())
        onView(withText("3")).check(matches(isDisplayed()))

        pressBack()
        pressBack()
        pressBack()

        onView(withText("0")).check(matches(isDisplayed()))
    }

    @Test
    fun viewStateWhenRotating() {
        setPortrait()

        onView(withHint("Type some text")).perform(typeText("Hello!"), closeSoftKeyboard())

        rotate()

        onView(withText(containsString("Hello!"))).check((matches(isDisplayed())))
    }

    @Test
    fun viewStateWhenNavigating() {
        onView(withHint("Type some text")).perform(typeText("Hello!"), closeSoftKeyboard())

        onView(withText("Next")).perform(click())
        pressBack()

        onView(withText(containsString("Hello!"))).check((matches(isDisplayed())))
    }

    private fun setPortrait() {
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sleep(100)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun rotate() {
        rule.activity.requestedOrientation = when (rule.activity.requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> error("Unknown state: ${rule.activity.requestedOrientation}")
        }
        sleep(100)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}