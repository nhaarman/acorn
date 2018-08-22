package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class StackNavigatorTest {

    private val scene1 = spy(TestScene(1))
    private val scene2 = spy(TestScene(2))

    private val navigator = TestStackNavigator(listOf(scene1))
    private val listener = TestListener()

    @Nested
    inner class TestNavigatorState {

        @Test
        fun `inactive navigator is not finished`() {
            /* When */
            navigator.addListener(listener)

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `inactive navigator does not notify newly added listener of scene`() {
            /* When */
            navigator.addListener(listener)

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `active navigator does notify newly added listener of scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.addListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `starting navigator does not finish`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `stopping navigator does not finish`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.onStop()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `destroying navigator does not finish`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.onDestroy()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `pushing a scene for inactive navigator does not notify listeners`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `pushing a scene for destroyed navigator does not notify listeners`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onDestroy()

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `pushing a scene for active navigator does notify listeners`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()

            /* When */
            navigator.push(scene2)

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `start navigator after scene push notifies pushed scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.push(scene2)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `popping from single item stack for inactive navigator notifies finished`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `popping from multi item stack for inactive navigator does not notify finished`() {
            /* Given */
            navigator.addListener(listener)
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `popping from multi item stack for inactive navigator does not notify scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `popping from single item stack for active navigator notifies finished`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `popping from multi item stack for active navigator does not notify finished`() {
            /* Given */
            navigator.addListener(listener)
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
            navigator.addListener(listener)
            navigator.onStart()
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `popping from multi item stack for destroyed navigator does not notify scene`() {
            /* Given */
            navigator.addListener(listener)
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
            navigator.addListener(listener)
            navigator.onStart()

            /* When */
            val result = navigator.onBackPressed()

            /* Then */
            expect(result).toBe(true)
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `onBackPressed for single scene stack for inactive navigator notifies listeners of finished`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            val result = navigator.onBackPressed()

            /* Then */
            expect(result).toBe(true)
            expect(listener.finished).toBe(true)
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
            restoredNavigator.addListener(listener)

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
            restoredNavigator.addListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(42)
        }
    }

    class TestStackNavigator(
        private val initialStack: List<TestScene>,
        savedState: NavigatorState? = null
    ) : StackNavigator<Navigator.Events>(savedState) {

        override fun initialStack(): List<Scene<out Container>> {
            return initialStack
        }

        override fun instantiateScene(sceneClass: Class<Scene<*>>, state: SceneState?): Scene<*> {
            return when (sceneClass) {
                TestScene::class.java -> TestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    private class TestListener : Navigator.Events {

        val scenes = mutableListOf<Scene<out Container>>()
        val lastScene get() = scenes.lastOrNull() as TestScene?

        var finished = false

        override fun scene(scene: Scene<out Container>) {
            scenes += scene
        }

        override fun finished() {
            finished = true
        }
    }
}
