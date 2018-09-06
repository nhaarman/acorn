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

package com.nhaarman.bravo.samples.hellostaterestoration

import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.containerState
import com.nhaarman.bravo.state.get
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

class HelloStateRestorationSceneTest {

    private val container = TestContainer()
    private val listener = mock<HelloStateRestorationScene.Events>()

    @Test
    fun `counter value is set`() {
        /* Given */
        val scene = HelloStateRestorationScene.create(3, listener)

        /* When */
        scene.attach(container)

        /* Then */
        expect(container.counterValue).toBe(3)
    }

    @Test
    fun `clicking next notifies listener`() {
        /* Given */
        val scene = HelloStateRestorationScene.create(3, listener)
        scene.attach(container)

        /* When */
        container.clickNext()

        /* Then */
        verify(listener).nextRequested()
    }

    @Test
    fun `restoring container state`() {
        val scene = HelloStateRestorationScene.create(3, listener)
        scene.attach(container)
        container.state = 42

        val container2 = TestContainer()

        /* When */
        scene.detach(container)
        scene.attach(container2)

        /* Then */
        expect(container2.state).toBe(42)
    }

    @Test
    fun `restoring scene from state`() {
        /* Given */
        val scene = HelloStateRestorationScene.create(1337, listener)
        val savedState = scene.saveInstanceState()

        /* When */
        val scene2 = HelloStateRestorationScene.create(savedState, listener)
        scene2.attach(container)

        /* Then */
        expect(container.counterValue).toBe(1337)
    }

    private class TestContainer : HelloStateRestorationContainer {

        var state = 0

        override var counterValue: Int = 0

        fun clickNext() {
            listener?.invoke()
        }

        private var listener: (() -> Unit)? = null
        override fun onNextClicked(f: () -> Unit) {
            listener = f
        }

        override fun saveInstanceState(): ContainerState {
            return containerState { it["state"] = state }
        }

        override fun restoreInstanceState(bundle: ContainerState) {
            state = bundle["state"] ?: 0
        }
    }
}