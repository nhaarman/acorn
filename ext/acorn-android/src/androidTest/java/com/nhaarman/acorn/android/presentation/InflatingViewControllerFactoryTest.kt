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

package com.nhaarman.acorn.android.presentation

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.acorn.android.test.R
import com.nhaarman.expect.expect
import org.junit.Test
import com.nhaarman.acorn.android.presentation.internal.InflatingViewControllerFactory
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

internal class InflatingViewControllerFactoryTest {

    @Test
    fun properViewIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
        val factory = InflatingViewControllerFactory<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = factory.viewControllerFor(TestScene(SceneKey("test")), viewGroup)

        /* Then */
        expect(result.view).toBeInstanceOf<LinearLayout>()
    }

    @Test
    fun properContainerIsReturned() {
        /* Given */
        val viewGroup = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
        val factory = InflatingViewControllerFactory<View>(R.layout.linearlayout) {
            MyContainer(it)
        }

        /* When */
        val result = factory.viewControllerFor(TestScene(SceneKey("test")), viewGroup)

        /* Then */
        expect(result).toBeInstanceOf<MyContainer> {
            expect(it.view).toBe(result.view)
        }
    }

    private class MyContainer(override val view: View) : ViewController
    private class TestScene(override val key: SceneKey) : Scene<Container>
}