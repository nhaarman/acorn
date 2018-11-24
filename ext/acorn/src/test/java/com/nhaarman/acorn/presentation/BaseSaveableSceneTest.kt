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

package com.nhaarman.acorn.presentation

import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.containerState
import com.nhaarman.acorn.state.get
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class BaseSaveableSceneTest {

    private val view1 = TestView()
    private val view2 = TestView()

    @Test
    fun `view state is restored when new view attaches`() {
        /* Given */
        val scene = TestSaveableScene(null)
        scene.attach(view1)
        view1.state = 3

        /* When */
        scene.detach(view1)
        scene.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    @Test
    fun `view state is restored from scene state`() {
        /* Given */
        val scene1 = TestSaveableScene(null)

        scene1.attach(view1)
        view1.state = 3

        /* When */
        scene1.detach(view1)
        val state = scene1.saveInstanceState()
        val scene2 = TestSaveableScene(state)
        scene2.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    @Test
    fun `view state is restored from scene state -- without detach`() {
        /* Given */
        val scene1 = TestSaveableScene(null)

        scene1.attach(view1)
        view1.state = 3

        /* When */
        val state = scene1.saveInstanceState()
        val scene2 = TestSaveableScene(state)
        scene2.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestSaveableScene(viewState: SceneState?) : BaseSaveableScene<TestView>(viewState)
    private class TestView : Container, RestorableContainer {

        var state: Int? = null

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
