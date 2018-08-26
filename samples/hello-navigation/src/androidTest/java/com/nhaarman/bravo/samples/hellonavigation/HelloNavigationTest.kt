package com.nhaarman.bravo.samples.hellonavigation

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class HelloNavigationTest {

    @Rule @JvmField val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun navigatingThroughScenes() {
        onView(withText("Hello, first Scene!")).check(matches(isDisplayed()))
        onView(withText("Second Scene")).perform(click())
        onView(withText("Hello, second Scene!")).check(matches(isDisplayed()))
        onView(withText("First Scene")).perform(click())
        onView(withText("Hello, first Scene!")).check(matches(isDisplayed()))
    }
}