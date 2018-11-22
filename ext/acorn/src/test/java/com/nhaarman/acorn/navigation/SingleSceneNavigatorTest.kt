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

package com.nhaarman.acorn.navigation

import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SingleSceneNavigatorTest {

    val navigator = TestSingleSceneNavigator(null)

    private val listener = TestListener()

    @Nested
    inner class TestNavigatorState {

        @Test
        fun `inactive navigator is not finished`() {
            /* When */
            navigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `inactive navigator is not destroyed`() {
            /* Then */
            expect(navigator.isDestroyed()).toBe(false)
        }

        @Test
        fun `active navigator is not destroyed`() {
            /* Given */
            navigator.onStart()

            /* Then */
            expect(navigator.isDestroyed()).toBe(false)
        }

        @Test
        fun `stopped navigator is not destroyed`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            expect(navigator.isDestroyed()).toBe(false)
        }

        @Test
        fun `destroyed navigator is destroyed`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            expect(navigator.isDestroyed()).toBe(true)
        }

        @Test
        fun `inactive navigator does not notify newly added listener of scene`() {
            /* When */
            navigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `active navigator does notify newly added listener of scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(navigator.scene)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(navigator.scene)
        }

        @Test
        fun `starting navigator does not finish`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `stopping navigator does not finish`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onStop()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `destroying navigator does not finish`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onDestroy()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `onBackPressed notifies listeners of finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            val result = navigator.onBackPressed()

            /* Then */
            expect(result).toBe(true)
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `finish notifies listeners of finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.finish()

            /* Then */
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `onBackPressed after navigator is destroyed does not notify listeners`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.onDestroy()

            /* When */
            val result = navigator.onBackPressed()

            /* Then */
            expect(result).toBe(false)
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `finish after navigator is destroyed does not notify listeners`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.onDestroy()

            /* When */
            navigator.finish()

            /* Then */
            expect(listener.finished).toBe(false)
        }
    }

    @Nested
    inner class State {

        @Test
        fun `starting navigator starts Scene`() {
            /* When */
            navigator.onStart()

            /* Then */
            navigator.scene.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator multiple times starts Scene only once`() {

            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            navigator.scene.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop Scene`() {
            /* When */
            navigator.onStop()

            /* Then */
            verifyNoMoreInteractions(navigator.scene)
        }

        @Test
        fun `stopping an active navigator stops Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            navigator.scene.inOrder {
                verify().onStart()
                verify().onStop()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop Scene`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator.scene, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy Scene`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator.scene).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            navigator.scene.inOrder {
                verify().onStart()
                verify().onStop()
                verify().onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start Scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStart()

            /* Then */
            verify(navigator.scene).onDestroy()
            verify(navigator.scene, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start Scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator.scene).onDestroy()
            verify(navigator.scene, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys Scene once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator.scene, times(1)).onDestroy()
        }

        @Test
        fun `starting and stopping navigator multiple times and finally destroying`() {
            /* When */
            navigator.onStart()
            navigator.onStop()
            navigator.onStart()
            navigator.onStop()
            navigator.onStart()
            navigator.onDestroy()

            /* Then */
            navigator.scene.inOrder {
                verify().onStart()
                verify().onStop()
                verify().onStart()
                verify().onStop()
                verify().onStart()
                verify().onStop()
                verify().onDestroy()
                verifyNoMoreInteractions()
            }
        }
    }

    @Nested
    inner class SavingState {

        @Test
        fun `saving and restoring state`() {
            /* Given */
            navigator.onStart()
            navigator.scene.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = TestSingleSceneNavigator(bundle)
            restoredNavigator.onStart()

            /* Then */
            expect(restoredNavigator.scene.foo).toBe(3)
        }
    }

    private class TestListener : Navigator.Events {

        val scenes = mutableListOf<Scene<out Container>>()
        val lastScene get() = scenes.lastOrNull() as SavableTestScene?

        var finished = false

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            scenes += scene
        }

        override fun finished() {
            finished = true
        }
    }

    class TestSingleSceneNavigator(savedState: NavigatorState?) : SingleSceneNavigator(savedState) {

        lateinit var scene: SavableTestScene

        override fun createScene(state: SceneState?): Scene<out Container> {
            return spy(SavableTestScene.create(state)).also { scene = it }
        }
    }
}
