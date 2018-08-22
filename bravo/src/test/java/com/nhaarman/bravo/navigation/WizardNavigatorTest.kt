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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class WizardNavigatorTest {

    private val scene1 = spy(TestScene(1))
    private val scene2 = spy(TestScene(2))
    private val scene3 = spy(TestScene(3))

    private val navigator = TestWizardNavigator(listOf(scene1, scene2))
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
        fun `going to next scene for inactive navigator does not notify listeners`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.next()

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `going to next scene for destroyed navigator does not notify listeners`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onDestroy()

            /* When */
            navigator.next()

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `going to next scene for active navigator does notify listeners`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()

            /* When */
            navigator.next()

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `start navigator after going to next scene notifies pushed scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.next()

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `going to previous screen when on first screen does not finish`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.previous()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `going back from second screen does not finish`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()
            navigator.next()

            /* When */
            navigator.previous()

            /* Then */
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `going back from second screen for active navigator notifies proper scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()
            navigator.next()

            /* When */
            navigator.previous()

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `going back from second screen for destroyed navigator does not notify scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onDestroy()
            navigator.next()

            /* When */
            navigator.previous()

            /* Then */
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `going to next for last scene notifies for active navigator notifies finished`() {
            /* Given */
            navigator.addListener(listener)
            navigator.next()

            /* When */
            navigator.next()

            /* Then */
            expect(listener.finished).toBe(true)
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
    inner class StateForSingleSceneWizard {

        private val navigator = TestWizardNavigator(listOf(scene1))

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
    inner class StateForMultiSceneWizard {

        private val navigator = TestWizardNavigator(listOf(scene1, scene2, scene3))

        @BeforeEach
        fun beforeEach() {
            navigator.next()
        }

        @Test
        fun `starting navigator starts current scene`() {
            /* When */
            navigator.onStart()

            /* Then */
            scene2.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator does not start non active scenes`() {
            /* When */
            navigator.onStart()

            /* Then */
            verifyZeroInteractions(scene1)
            verifyZeroInteractions(scene3)
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
            verify(scene3, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy all created Scenes`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2, scene3) {
                verify(scene3, never()).onDestroy()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
            }
        }

        @Test
        fun `destroy an inactive navigator does destroy all Scenes`() {
            /* Given */
            // Initialize scene3
            navigator.next()
            navigator.previous()

            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2, scene3) {
                verify(scene3).onDestroy()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
            }
        }

        @Test
        fun `destroy an active navigator stops top Scene and destroys all created Scenes`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2, scene3) {
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `destroy an active navigator stops top Scene and destroys all Scenes`() {
            /* Given */
            // Initialize scene3
            navigator.next()
            navigator.previous()
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2, scene3) {
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene3).onDestroy()
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
    inner class StatesWhenManipulatingWizard {

        private val navigator = TestWizardNavigator(listOf(scene1, scene2))

        @Test
        fun `going back when on first scene does not destroy scene`() {
            /* When */
            navigator.previous()

            /* When */
            verify(scene1, never()).onDestroy()
        }

        @Test
        fun `going back when on first scene for inactive navigator does not stop scene`() {
            /* When */
            navigator.previous()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `going back when on second screen for inactive navigator does not destroy second scene`() {
            /* Given */
            navigator.next()

            /* When */
            navigator.previous()

            /* When */
            verify(scene2, never()).onDestroy()
        }

        @Test
        fun `going back when on first scene for active navigator does not stop or destroy scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.previous()

            /* When */
            verify(scene1, never()).onStop()
            verify(scene1, never()).onDestroy()
        }

        @Test
        fun `going to previous scene when on second scene for active navigator stops latest scene, and starts current scene`() {
            /* Given */
            navigator.next()
            navigator.onStart()

            /* When */
            navigator.previous()

            /* When */
            inOrder(scene1, scene2) {
                verify(scene2).onStop()
                verify(scene2, never()).onDestroy()
                verify(scene1).onStart()
            }
        }

        @Test
        fun `onBackPressed when on first screen for inactive navigator destroys scene`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene1).onDestroy()
        }

        @Test
        fun `onBackPressed when on first screen for inactive navigator does not stop scene`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `onBackPressed when on second screen for inactive navigator does not destroy second scene`() {
            /* Given */
            navigator.next()

            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene2, never()).onDestroy()
        }

        @Test
        fun `onBackPressed when on first screen for active navigator stops and destroys scene`() {
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
        fun `onBackPressed when on second screen for active navigator stops second scene, and starts current scene`() {
            /* Given */
            navigator.next()
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            inOrder(scene1, scene2) {
                verify(scene2).onStop()
                verify(scene2, never()).onDestroy()
                verify(scene1).onStart()
            }
        }

        @Test
        fun `going to next screen for inactive navigator does not stop previous`() {
            /* When */
            navigator.next()

            /* Then */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `going to next screen for inactive navigator does not start scene`() {
            /* When */
            navigator.next()

            /* Then */
            verify(scene2, never()).onStart()
        }

        @Test
        fun `going to next screen for destroyed navigator does not start scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.next()

            /* Then */
            verify(scene2, never()).onStart()
        }

        @Test
        fun `going to next scene for started navigator stops previous scene and starts pushed scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.next()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene1).onStop()
                verify(scene2).onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `going to next screen when on last scene stops last scene and destroys all`() {
            /* Given */
            navigator.onStart()
            navigator.next()

            /* When */
            navigator.next()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene1).onStop()
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
            }
        }
    }

    @Nested
    inner class SavingState {

        private val scene1 = TestScene(1)
        private val scene2 = TestScene(2)

        private val navigator = TestWizardNavigator(listOf(scene1, scene2))

        @Test
        fun `saving and restoring state for single scene stack`() {
            /* Given */
            navigator.onStart()
            scene1.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()

            scene1.foo = 6
            val restoredNavigator = TestWizardNavigator(listOf(scene1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(3)
        }

        @Test
        fun `saving and restoring state for when on second screen`() {
            /* Given */
            navigator.onStart()
            scene1.foo = 3
            scene2.foo = 42
            navigator.next()

            /* When */
            val bundle = navigator.saveInstanceState()
            scene1.foo = 6
            scene2.foo = 8

            val restoredNavigator = TestWizardNavigator(listOf(scene1, scene2), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(42)
        }
    }

    class TestWizardNavigator(
        private val initialStack: List<TestScene>,
        savedState: NavigatorState? = null
    ) : WizardNavigator<Navigator.Events>(savedState) {

        override fun createScene(index: Int): Scene<out Container>? {
            return initialStack.getOrNull(index)
        }

        override fun instantiateScene(sceneClass: Class<*>, state: SceneState?): Scene<*> {
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
