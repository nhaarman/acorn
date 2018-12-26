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

package com.nhaarman.acorn.presentation

import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.containerState
import com.nhaarman.acorn.state.get
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class BaseSavableSceneTest {

    private val view1 = TestView()
    private val view2 = TestView()

    @Test
    fun `view state is restored when new view attaches`() {
        /* Given */
        val scene = TestSavableScene(null)
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
        val scene1 = TestSavableScene(null)

        scene1.attach(view1)
        view1.state = 3

        /* When */
        scene1.detach(view1)
        val state = scene1.saveInstanceState()
        val scene2 = TestSavableScene(state)
        scene2.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    @Test
    fun `view state is restored from scene state -- without detach`() {
        /* Given */
        val scene1 = TestSavableScene(null)

        scene1.attach(view1)
        view1.state = 3

        /* When */
        val state = scene1.saveInstanceState()
        val scene2 = TestSavableScene(state)
        scene2.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestSavableScene(viewState: SceneState?) : BaseSavableScene<TestView>(viewState)
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
