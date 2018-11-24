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

package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.containerState
import com.nhaarman.bravo.state.get
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class BasicSceneTest {

    private val scene = TestBasicScene()
    private val testView = TestView()

    @Test
    fun `attaching a container stores the view`() {
        /* When */
        scene.attach(testView)

        /* Then */
        expect(scene.view).toBe(testView)
    }

    @Test
    fun `detaching the container releases the view`() {
        /* Given */
        scene.attach(testView)

        /* When */
        scene.detach(testView)

        /* Then */
        expect(scene.view).toBeNull()
    }

    @Test
    fun `view state is restored between views`() {
        /* Given */
        val view1 = TestView(1)
        val view2 = TestView(2)

        /* When */
        scene.attach(view1)
        view1.state = 3
        scene.detach(view1)
        scene.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestBasicScene : BasicScene<TestView>() {

        val view get() = currentView
    }

    private class TestView(var state: Int? = null) : Container, RestorableContainer {

        override fun saveInstanceState(): ContainerState {
            return containerState {
                it["state"] = state
            }
        }

        override fun restoreInstanceState(bundle: ContainerState) {
            state = bundle["state"]
        }
    }
}
