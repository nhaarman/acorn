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

package com.nhaarman.acorn.samples.hellostaterestoration

import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep

class HelloStateRestorationTest {

    @Rule @JvmField
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

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
