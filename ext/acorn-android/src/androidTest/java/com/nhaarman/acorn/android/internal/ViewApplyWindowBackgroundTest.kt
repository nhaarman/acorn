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

package com.nhaarman.acorn.android.internal

import android.graphics.drawable.ColorDrawable
import androidx.test.rule.ActivityTestRule
import android.view.View
import com.nhaarman.acorn.android.test.R
import com.nhaarman.acorn.android.TestActivity
import com.nhaarman.expect.expect
import org.junit.Rule
import org.junit.Test

class ViewApplyWindowBackgroundTest {

    @JvmField @Rule val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    val activity get() = rule.activity

    @Test
    fun nullWindowBackground() {
        /* Given */
        activity.setTheme(R.style.NullWindowBackground)
        val view = View(rule.activity)

        /* When */
        view.applyWindowBackground()

        /* Then */
        expect(view.background).toBeNull()
    }

    @Test
    fun noWindowBackground() {
        /* Given */
        activity.setTheme(R.style.NullWindowBackground)
        val view = View(rule.activity)

        /* When */
        view.applyWindowBackground()

        /* Then */
        expect(view.background).toBeNull()
    }

    @Test
    fun whiteColorWindowBackground() {
        rule.activity.setTheme(R.style.WhiteColorWindowBackground)

        /* Given */
        val view = View(rule.activity)
        view.applyWindowBackground()

        expect(view.background).toBeInstanceOf<ColorDrawable> {
            expect(it.color).toBe(activity.getColor(R.color.white))
        }
    }

    @Test
    fun rgb8ColorWindowBackground() {
        rule.activity.setTheme(R.style.Rgb8ColorWindowBackground)

        /* Given */
        val view = View(rule.activity)
        view.applyWindowBackground()

        expect(view.background).toNotBeNull()
    }

    @Test
    fun drawableWindowBackground() {
        rule.activity.setTheme(R.style.DrawableWindowBackground)

        /* Given */
        val view = View(rule.activity)
        view.applyWindowBackground()

        expect(view.background).toNotBeNull()
    }
}