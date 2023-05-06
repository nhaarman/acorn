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

package com.nhaarman.acorn.notesapp.android.ui.createitem

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.nhaarman.acorn.android.tests.AcornViewTestRule
import com.nhaarman.acorn.notesapp.android.R
import com.nhaarman.acorn.notesapp.android.ViewFactoryProvider
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemContainer
import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.expect.expect
import com.nhaarman.expect.lastValue
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Rule
import org.junit.Test

class CreateItemViewTest {

    @Rule @JvmField
    val rule = AcornViewTestRule<CreateItemContainer>(
        ViewFactoryProvider.viewFactory,
        CreateItemScene.key,
    )

    @Test
    fun settingInitialText() {
        // When
        rule.onUiThread { container.setInitialText("Hello!") }

        // Then
        onView(withText("Hello!")).check(matches(isDisplayed()))
    }

    @Test
    fun inputText() {
        // Given
        val observer = rule.container.textChanges.test()

        // When
        onView(withHint("Take a note")).perform(typeText("Hello, world!"))

        // Then
        expect(observer.lastValue.trim()).toBe("Hello, world!")
    }

    @Test
    fun clickCreate() {
        // Given
        val observer = rule.container.createClicks.subscribeOn(AndroidSchedulers.mainThread()).test()

        // When
        onView(withId(R.id.save)).perform(click())

        // Then
        expect(observer.valueCount()).toBe(1)
    }
}
