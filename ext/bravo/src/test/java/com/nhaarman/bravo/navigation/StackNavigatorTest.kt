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

package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.navigatorState
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

internal class StackNavigatorTest {

    private val scene1 = spy(TestScene(1))
    private val scene2 = spy(TestScene(2))
    private val scene3 = spy(TestScene(3))

    private val navigator = TestStackNavigator(listOf(scene1))
    private val listener = spy(TestListener())

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
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `starting navigator multiple times notifies listeners of scene only once`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            verify(listener, times(1)).scene(any(), anyOrNull())
        }

        @Test
        fun `starting navigator - scene notification has no transition data`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastTransitionData).toBeNull()
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
        fun `pushing a scene for inactive navigator does not notify listeners`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `pushing a scene for destroyed navigator does not notify listeners`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onDestroy()

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `pushing a scene for active navigator does notify listeners`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `pushing a scene for active navigator - scene notification has forward transition data`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastTransitionData?.isBackwards).toBe(false)
        }

        @Test
        fun `start navigator after scene push notifies pushed scene`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.push(scene2)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `popping from single item stack for inactive navigator notifies finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `popping from multi item stack for inactive navigator does not notify finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `popping from multi item stack for inactive navigator does not notify scene`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `popping from single item stack for active navigator notifies finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `popping from multi item stack for active navigator does not notify finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `popping from multi item stack for active navigator notifies proper scene`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `popping from multi item stack for active navigator - scene notification has backward transition data`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.lastTransitionData?.isBackwards).toBe(true)
        }

        @Test
        fun `popping from multi item stack for destroyed navigator does not notify scene`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onDestroy()
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `onBackPressed for single scene stack notifies listeners of finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()

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
        fun `onBackPressed for single scene stack for inactive navigator notifies listeners of finished`() {
            /* Given */
            navigator.addNavigatorEventsListener(listener)

            /* When */
            val result = navigator.onBackPressed()

            /* Then */
            expect(result).toBe(true)
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `onBackPressed for single scene stack after navigator is destroyed does not notify listeners`() {
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
    inner class StateForSingleSceneStack {

        @Test
        fun `starting navigator starts Scene`() {
            /* When */
            navigator.onStart()

            /* Then */
            scene1.inOrder {
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
            scene1.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop Scene`() {
            /* When */
            navigator.onStop()

            /* Then */
            verifyNoMoreInteractions(scene1)
        }

        @Test
        fun `stopping an active navigator stops Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            scene1.inOrder {
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
            verify(scene1, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy Scene`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(scene1).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            scene1.inOrder {
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
            verify(scene1).onDestroy()
            verify(scene1, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start Scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(scene1).onDestroy()
            verify(scene1, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys Scene once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(scene1, times(1)).onDestroy()
        }
    }

    @Nested
    inner class StateForMultiSceneStack {

        private val navigator = TestStackNavigator(listOf(scene1, scene2))

        @Test
        fun `starting navigator starts top Scene`() {
            /* When */
            navigator.onStart()

            /* Then */
            scene2.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator does not start bottom scenes`() {
            /* When */
            navigator.onStart()

            /* Then */
            verifyZeroInteractions(scene1)
        }

        @Test
        fun `starting navigator multiple times starts Scene only once`() {
            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            scene2.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop Scene`() {
            /* When */
            navigator.onStop()

            /* Then */
            verifyNoMoreInteractions(scene2)
        }

        @Test
        fun `stopping an active navigator stops Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            scene2.inOrder {
                verify().onStart()
                verify().onStop()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop Scenes`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(scene1, never()).onStop()
            verify(scene2, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy Scenes`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
            }
        }

        @Test
        fun `destroy an active navigator stops top Scene and destroys all Scenes`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
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
            verify(scene2).onDestroy()
            verify(scene2, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start Scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(scene2).onDestroy()
            verify(scene2, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys Scene once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(scene2, times(1)).onDestroy()
        }
    }

    @Nested
    inner class StatesWhenManipulatingStack {

        @Test
        fun `popping from a single item stack for inactive navigator destroys scene`() {
            /* When */
            navigator.pop()

            /* When */
            verify(scene1).onDestroy()
        }

        @Test
        fun `popping from a single item stack for inactive navigator does not stop scene`() {
            /* When */
            navigator.pop()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `popping from a multi item stack for inactive navigator destroys latest scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))

            /* When */
            navigator.pop()

            /* When */
            verify(scene2).onDestroy()
        }

        @Test
        fun `popping from a single item stack for active navigator stops and destroys scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            scene1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `popping from a multi item stack for active navigator stops and destroys latest scene, and starts current scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            inOrder(scene1, scene2) {
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onStart()
            }
        }

        @Test
        fun `replacing top item from a single item stack for inactive navigator destroys original scene`() {
            /* When */
            navigator.replace(scene2)

            /* When */
            verify(scene1).onDestroy()
        }

        @Test
        fun `replacing top item from a single item stack for inactive navigator does not stop original scene`() {
            /* When */
            navigator.replace(scene2)

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `replacing top item from a multi item stack for inactive navigator destroys latest scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))

            /* When */
            navigator.replace(scene3)

            /* When */
            verify(scene2).onDestroy()
        }

        @Test
        fun `replacing top item from a multi item stack for inactive navigator does not start replacing scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))

            /* When */
            navigator.replace(scene3)

            /* When */
            verifyNoMoreInteractions(scene3)
        }

        @Test
        fun `replacing top item from a single item stack for active navigator stops and destroys original scene, and starts replacing scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.replace(scene2)

            /* When */
            inOrder(scene1, scene2) {
                verify(scene1).onStop()
                verify(scene1).onDestroy()
                verify(scene2).onStart()
            }
        }

        @Test
        fun `replacing top item from a multi item stack for active navigator stops and destroys latest scene, and starts replacing scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))
            navigator.onStart()

            /* When */
            navigator.replace(scene3)

            /* When */
            inOrder(scene1, scene2, scene3) {
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene3).onStart()
            }
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator destroys scene`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene1).onDestroy()
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator does not stop scene`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `onBackPressed from a multi item stack for inactive navigator destroys latest scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))

            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene2).onDestroy()
        }

        @Test
        fun `onBackPressed from a single item stack for active navigator stops and destroys scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            scene1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `onBackPressed from a multi item stack for active navigator stops and destroys latest scene, and starts current scene`() {
            /* Given */
            val navigator = TestStackNavigator(listOf(scene1, scene2))
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            inOrder(scene1, scene2) {
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onStart()
            }
        }

        @Test
        fun `pushing for inactive navigator does not stop previous`() {
            /* When */
            navigator.push(scene2)

            /* Then */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `pushing for inactive navigator does not start scene`() {
            /* When */
            navigator.push(scene2)

            /* Then */
            verify(scene2, never()).onStart()
        }

        @Test
        fun `pushing for destroyed navigator does not start scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.push(scene2)

            /* Then */
            verify(scene2, never()).onStart()
        }

        @Test
        fun `pushing for started navigator stops previous scene and starts pushed scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.push(scene2)

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene1).onStop()
                verify(scene2).onStart()
                verifyNoMoreInteractions()
            }
        }
    }

    @Nested
    inner class SavingState {

        private val scene1 = TestScene(1)
        private val scene2 = TestScene(2)

        private val navigator = TestStackNavigator(listOf(scene1))

        @Test
        fun `saving and restoring state for single scene stack`() {
            /* Given */
            navigator.onStart()
            scene1.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()
            scene1.foo = 6

            val restoredNavigator = TestStackNavigator(listOf(scene1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(3)
        }

        @Test
        fun `saving and restoring state for multi scene stack`() {
            /* Given */
            navigator.onStart()
            scene1.foo = 3
            scene2.foo = 42
            navigator.push(scene2)

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = TestStackNavigator(listOf(scene1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(42)
        }

        @Test
        fun `restoring from empty state ignores state`() {
            /* When */
            val result = TestStackNavigator(listOf(scene1), NavigatorState())
            result.onStart()
            result.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `restoring from invalid state ignores state - size = 0`() {
            /* Given */
            val state = navigatorState {
                it["size"] = 0
            }

            /* When */
            val result = TestStackNavigator(listOf(scene1), state)
            result.onStart()
            result.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }
    }

    class TestStackNavigator(
        private val initialStack: List<TestScene>,
        savedState: NavigatorState? = null
    ) : StackNavigator(savedState) {

        override fun initialStack(): List<Scene<out Container>> {
            return initialStack
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<*> {
            return when (sceneClass) {
                TestScene::class -> TestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    private open class TestListener : Navigator.Events {

        val scenes = mutableListOf<Pair<Scene<out Container>, TransitionData?>>()
        val lastScene get() = scenes.lastOrNull()?.first as TestScene?
        val lastTransitionData get() = scenes.lastOrNull()?.second

        var finished = false

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            scenes += scene to data
        }

        override fun finished() {
            finished = true
        }
    }
}
