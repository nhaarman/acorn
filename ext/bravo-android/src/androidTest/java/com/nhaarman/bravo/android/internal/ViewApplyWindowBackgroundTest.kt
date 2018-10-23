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

package com.nhaarman.bravo.android.internal

import android.graphics.drawable.ColorDrawable
import androidx.test.rule.ActivityTestRule
import android.view.View
import com.nhaarman.bravo.android.test.R
import com.nhaarman.bravo.android.TestActivity
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