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

import androidx.lifecycle.Lifecycle
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class LifecycleSceneTest {

    private val scene = TestLifecycleScene()
    val currentState get() = scene.lifecycle.currentState

    @Test
    fun `initially the state is created`() {
        expect(currentState).toBe(Lifecycle.State.CREATED)
    }

    @Test
    fun `after onStart the state is started`() {
        /* When */
        scene.onStart()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.STARTED)
    }

    @Test
    fun `after onStop the state is created`() {
        /* When */
        scene.onStart()
        scene.onStop()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.CREATED)
    }

    @Test
    fun `after onStop and onStart the state is started`() {
        /* When */
        scene.onStart()
        scene.onStop()
        scene.onStart()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.STARTED)
    }

    @Test
    fun `after onDestroy the state is destroyed`() {
        /* When */
        scene.onDestroy()

        /* Then */
        expect(currentState).toBe(Lifecycle.State.DESTROYED)
    }

    private class TestLifecycleScene(
        savedState: SceneState? = null
    ) : LifecycleScene<RestorableContainer>(savedState)
}
