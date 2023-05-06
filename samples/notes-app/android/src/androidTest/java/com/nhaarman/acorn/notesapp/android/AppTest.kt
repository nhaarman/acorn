/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
import org.junit.Rule
import org.junit.Test

class AppTest {

    @Rule @JvmField
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Rule @JvmField
    val purgeDatabase = PurgeDatabaseRule()

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
