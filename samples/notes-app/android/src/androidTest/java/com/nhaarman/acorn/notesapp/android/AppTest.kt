/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.notesapp.android

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.recyclerview.widget.RecyclerView
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
        onView(withId(R.id.itemsRecyclerView)).perform(scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(100))
        onView(withText("My note")).check(matches(isDisplayed()))
        onView(withText("My note")).perform(click())
        onView(withText("My note")).check(matches(isDisplayed()))
        onView(withId(R.id.delete)).perform(click())
        onView(withText("My note")).check(doesNotExist())
    }
}