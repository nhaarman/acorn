package com.nhaarman.bravo.android.internal

import android.graphics.drawable.ColorDrawable
import android.support.test.rule.ActivityTestRule
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