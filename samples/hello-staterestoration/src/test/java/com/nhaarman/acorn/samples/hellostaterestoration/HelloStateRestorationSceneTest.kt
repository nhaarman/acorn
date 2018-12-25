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

import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.containerState
import com.nhaarman.acorn.state.get
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