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

package com.nhaarman.bravo.samples.hellostartactivity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

class FirstSceneTest {

    private val listener = mock<FirstScene.Events>()
    private val scene = FirstScene(listener)
    private val container = TestContainer()

    @Test
    fun `clicking button notifies listener`() {
        /* Given */
        scene.attach(container)

        /* When */
        container.clickSecondScene()

        /* Then */
        verify(listener).mapsRequested()
    }

    private class TestContainer : FirstSceneContainer {

        fun clickSecondScene() {
            listener?.invoke()
        }

        private var listener: (() -> Unit)? = null
        override fun onButtonClicked(f: () -> Unit) {
            listener = f
        }
    }
}