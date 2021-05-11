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

import androidx.lifecycle.Lifecycle
import androidx.test.annotation.UiThreadTest
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.expect.expect
import org.junit.Test

class LifecycleSceneTest {

    private val scene = TestLifecycleScene()
    val currentState get() = scene.lifecycle.currentState

    @Test
    @UiThreadTest
    fun initiallyTheStateIsCreated() {
        expect(currentState).toBe(Lifecycle.State.CREATED)
    }

    @Test
    @UiThreadTest
    fun afterOnStartTheStateIsStarted() {
        /* When */
        scene.onStart()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.STARTED)
    }

    @Test
    @UiThreadTest
    fun afterOnStopTheStateIsCreated() {
        /* When */
        scene.onStart()
        scene.onStop()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.CREATED)
    }

    @Test
    @UiThreadTest
    fun afterOnStopAndOnStartTheStateIsStarted() {
        /* When */
        scene.onStart()
        scene.onStop()
        scene.onStart()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.STARTED)
    }

    @Test
    @UiThreadTest
    fun afterOnDestroyTheStateIsDestroyed() {
        /* When */
        scene.onDestroy()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.DESTROYED)
    }

    private class TestLifecycleScene(
        savedState: SceneState? = null
    ) : LifecycleScene<RestorableContainer>(savedState)
}
