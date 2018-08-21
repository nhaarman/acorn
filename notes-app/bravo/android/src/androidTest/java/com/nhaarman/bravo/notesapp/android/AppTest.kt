package com.nhaarman.bravo.notesapp.android

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withHint
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import org.junit.Rule
import org.junit.Test

class AppTest {

    @Rule @JvmField val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)
    @Rule @JvmField val purgeDatabase = PurgeDatabaseRule()

    @Test
    fun clickThroughScreens() {
        onView(withId(R.id.createButton)).perform(click())
        onView(withHint("Take a note")).perform(typeText("My note"))
        onView(withId(R.id.save)).perform(click())
        onView(withId(R.id.itemsRecyclerView)).perform(scrollToPosition<RecyclerView.ViewHolder>(100))
        onView(withText("My note")).check(matches(isDisplayed()))
        onView(withText("My note")).perform(click())
        onView(withText("My note")).check(matches(isDisplayed()))
        onView(withId(R.id.delete)).perform(click())
        onView(withText("My note")).check(doesNotExist())
    }
}