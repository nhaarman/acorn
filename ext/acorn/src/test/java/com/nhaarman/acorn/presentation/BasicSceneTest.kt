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
import com.nhaarman.acorn.state.containerState
import com.nhaarman.acorn.state.get
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

    @Test
    fun `view state is restored between scenes`() {
        /* Given */
        val view1 = TestView(1)
        val view2 = TestView(2)

        /* When */
        scene.attach(view1)
        view1.state = 3
        val state = view1.saveInstanceState()

        val newScene = TestBasicScene(state)
        newScene.attach(view2)

        /* Then */
        expect(view2.state).toBe(3)
    }

    private class TestBasicScene(
        containerState: ContainerState? = null
    ) : BasicScene<TestView>(
        containerState
    ) {

        val view get() = attachedView
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
