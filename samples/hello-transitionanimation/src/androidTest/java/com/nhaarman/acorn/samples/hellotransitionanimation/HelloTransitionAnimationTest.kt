/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.samples.hellotransitionanimation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class HelloTransitionAnimationTest {

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