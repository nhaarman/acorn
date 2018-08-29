package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
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
            expect(listener.lastScene).toBe(navigator.scene)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(navigator.scene)
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
        fun `onBackPressed notifies listeners of finished`() {
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
        val lastScene get() = scenes.lastOrNull() as TestScene?

        var finished = false

        override fun scene(scene: Scene<out Container>) {
            scenes += scene
        }

        override fun finished() {
            finished = true
        }
    }

    class TestSingleSceneNavigator(savedState: NavigatorState?) : SingleSceneNavigator<Navigator.Events>(savedState) {

        lateinit var scene: TestScene

        override fun createScene(state: SceneState?): Scene<out Container> {
            return spy(TestScene.create(state)).also { scene = it }
        }
    }
}
