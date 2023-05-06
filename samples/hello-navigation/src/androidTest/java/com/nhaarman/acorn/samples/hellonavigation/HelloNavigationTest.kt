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

package com.nhaarman.acorn.samples.hellonavigation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class HelloNavigationTest {

    @Rule @JvmField
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun navigatingThroughScenes() {
        onView(withText("Hello, first Scene!")).check(matches(isDisplayed()))
        onView(withText("Second Scene")).perform(click())
        onView(withText("Hello, second Scene!")).check(matches(isDisplayed()))
        onView(withText("First Scene")).perform(click())
        onView(withText("Hello, first Scene!")).check(matches(isDisplayed()))
    }
}
