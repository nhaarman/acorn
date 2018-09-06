package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CompositeReplacingNavigatorTest {

    private val navigator1Scene1 = spy(TestScene(11))
    private val navigator2Scene1 = spy(TestScene(21))
    private val navigator2Scene2 = spy(TestScene(22))

    private val navigator1 = spy(TestSingleSceneNavigator(navigator1Scene1))
    private val navigator2 = spy(TestStackNavigator(listOf(navigator2Scene1)))

    private val navigator = TestCompositeReplacingNavigator(navigator1)
    private val listener = mock<Navigator.Events>()

    @Nested
    inner class TestNavigatorState {

        @Nested
        inner class InactiveNavigator {

            @Test
            fun `navigator is not finished`() {
                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `navigator is not destroyed`() {
                /* Then */
                expect(navigator.isDestroyed()).toBe(false)
            }

            @Test
            fun `added listener does not get notified of scene`() {
                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `pushing a navigator does not notify listeners of scene`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.replace(navigator2)

                /* Then */
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `onBackPressed notifies listeners of finished`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener).finished()
            }

            @Test
            fun `onBackPressed does not notify screen`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `onBackPressed for replaced navigator does not notify screen`() {
                /* Given */
                navigator.addListener(listener)
                navigator.replace(navigator2)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener, never()).scene(any(), any())
            }
        }

        @Nested
        inner class ActiveNavigator {

            @Test
            fun `navigator is not finished`() {
                /* Given */
                navigator.onStart()

                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `navigator is not destroyed`() {
                /* Given */
                navigator.onStart()

                /* Then */
                expect(navigator.isDestroyed()).toBe(false)
            }

            @Test
            fun `added listener gets notified of scene`() {
                /* Given */
                navigator.onStart()

                /* When */
                navigator.addListener(listener)

                /* Then */
                verify(listener).scene(navigator1Scene1, null)
            }

            @Test
            fun `starting navigator notifies listeners of scene`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener).scene(navigator1Scene1, null)
            }

            @Test
            fun `starting navigator does not finish`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `replacing a navigator notifies listeners of scene`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onStart()

                /* When */
                navigator.replace(navigator2)

                /* Then */
                verify(listener).scene(eq(navigator2Scene1), anyOrNull())
            }

            @Test
            fun `start navigator after navigator replaced notifies pushed scene`() {
                /* Given */
                navigator.addListener(listener)
                navigator.replace(navigator2)

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener).scene(navigator2Scene1)
            }

            @Test
            fun `onBackPressed for a single scene notifies finished`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                verify(listener).finished()
            }

            @Test
            fun `onBackPressed for a single scene does not notify screen`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                listener.inOrder {
                    verify().scene(navigator1Scene1)
                    verify().finished()
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `onBackPressed for multiple navigators notifies proper scenes`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)
                navigator.replace(navigator2)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                listener.inOrder {
                    verify().scene(eq(navigator1Scene1), anyOrNull())
                    verify().scene(eq(navigator2Scene1), anyOrNull())
                    verify().finished()
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `forwards from nested navigator is propagated`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)
                navigator.replace(navigator2)

                /* When */
                navigator2.push(navigator2Scene2)

                /* Then */
                /* Then */
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())
                    expect(lastValue.isBackwards).toBe(false)
                }
            }

            @Test
            fun `backwards from nested navigator is propagated`() {
                /* Given */
                navigator.onStart()
                navigator.addListener(listener)
                navigator.replace(navigator2)
                navigator2.push(navigator2Scene2)

                /* When */
                navigator.onBackPressed()

                /* Then */
                /* Then */
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())
                    expect(lastValue.isBackwards).toBe(true)
                }
            }
        }

        @Nested
        inner class StoppedNavigator {

            @Test
            fun `stopping navigator does not finish`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onStop()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `stopped navigator is not destroyed`() {
                /* When */
                navigator.onStop()

                /* Then */
                expect(navigator.isDestroyed()).toBe(false)
            }
        }

        @Nested
        inner class DestroyedNavigator {

            @Test
            fun `destroying navigator does not finish`() {
                /* Given */
                navigator.addListener(listener)

                /* When */
                navigator.onDestroy()

                /* Then */
                verify(listener, never()).finished()
            }

            @Test
            fun `isDestroyed returns true`() {
                /* When */
                navigator.onDestroy()

                /* Then */
                expect(navigator.isDestroyed()).toBe(true)
            }

            @Test
            fun `pushing a scene for destroyed navigator does not notify listeners`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onDestroy()

                /* When */
                navigator.replace(navigator2)

                /* Then */
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `onBackPressed for replaced Navigator for destroyed navigator does not notify scene`() {
                /* Given */
                navigator.addListener(listener)
                navigator.onDestroy()
                navigator.replace(navigator2)

                /* When */
                navigator.onBackPressed()

                /* Then */
                verify(listener, never()).scene(any(), any())
            }
        }
    }

    @Nested
    inner class StateForSingleChildNavigatorStack {

        @Test
        fun `starting navigator starts child navigator`() {
            /* When */
            navigator.onStart()

            /* Then */
            navigator1.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator multiple times starts child navigator only once`() {
            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            verify(navigator1, times(1)).onStart()
        }

        @Test
        fun `stopping an inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `stopping an active navigator stops child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            navigator1.inOrder {
                verify().onStart()
                verify().onStop()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy child navigator`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            navigator1.inOrder {
                verify().onStart()
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start child navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStart()

            /* Then */
            verify(navigator1).onDestroy()
            verify(navigator1, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not stop child navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(navigator1).onDestroy()
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys child navigator once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(navigator1, times(1)).onDestroy()
        }
    }

    @Nested
    inner class StatesWhenManipulating {

        @Test
        fun `onBackPressed for inactive navigator destroys child navigator`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(navigator1).onDestroy()
        }

        @Test
        fun `onBackPressed for inactive navigator does not stop child navigator`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `onBackPressed for active navigator stops and destroys child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            navigator1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `replacing for inactive navigator does not stop previous child navigator`() {
            /* When */
            navigator.replace(navigator2)

            /* Then */
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `replacing for inactive navigator does not start pushed navigator`() {
            /* When */
            navigator.replace(navigator2)

            /* Then */
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `replacing for destroyed navigator does not start pushed navigator`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.replace(navigator2)

            /* Then */
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `replacing for started navigator stops previous child navigator and starts pushed child navigator`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.replace(navigator2)

            /* Then */
            inOrder(navigator1, navigator2) {
                verify(navigator1).onStart()
                verify(navigator1).onStop()
                verify(navigator2).onStart()
                verifyNoMoreInteractions()
            }
        }
    }

    @Nested
    inner class SavingState {

        private val navigator1Scene1 = TestScene(11)
        private val navigator1 = RestorableTestSingleSceneNavigator(navigator1Scene1)
        private val navigator = RestorableTestCompositeReplacingNavigator(navigator1, null)

        @Test
        fun `saving and restoring state for single navigator stack`() {
            /* Given */
            navigator.onStart()
            navigator1Scene1.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = RestorableTestCompositeReplacingNavigator(navigator1, bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture(), anyOrNull())
                expect(lastValue).toNotBeTheSameAs(navigator1Scene1)
                expect(lastValue).toBeInstanceOf<TestScene> {
                    expect(it.foo).toBe(3)
                }
            }
        }
    }

    class TestCompositeReplacingNavigator(
        private val initialNavigator: Navigator<out Navigator.Events>
    ) : CompositeReplacingNavigator<Navigator.Events>(null) {

        override fun initialNavigator(): Navigator<out Navigator.Events> {
            return initialNavigator
        }

        override fun instantiateNavigator(
            navigatorClass: Class<Navigator<*>>,
            state: NavigatorState?
        ): Navigator<out Navigator.Events> {
            error("Not supported")
        }
    }

    open class TestSingleSceneNavigator(
        private val scene: Scene<out Container>,
        savedState: NavigatorState? = null
    ) : SingleSceneNavigator<Navigator.Events>(savedState) {

        override fun createScene(state: SceneState?): Scene<out Container> {
            return scene
        }
    }

    open class TestStackNavigator(
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

    class RestorableTestCompositeReplacingNavigator(
        private val initialNavigator: Navigator<out Navigator.Events>,
        savedState: NavigatorState?
    ) : CompositeReplacingNavigator<Navigator.Events>(savedState) {

        override fun initialNavigator(): Navigator<out Navigator.Events> {
            return initialNavigator
        }

        override fun instantiateNavigator(
            navigatorClass: Class<Navigator<*>>,
            state: NavigatorState?
        ): Navigator<out Navigator.Events> {
            return when (navigatorClass) {
                RestorableTestSingleSceneNavigator::class.java -> RestorableTestSingleSceneNavigator(
                    TestScene(0),
                    state
                )
                TestStackNavigator::class.java -> TestStackNavigator(emptyList(), state)
                else -> error("Unknown navigator class: $navigatorClass.")
            }
        }
    }

    open class RestorableTestSingleSceneNavigator(
        private val scene: Scene<out Container>,
        savedState: NavigatorState? = null
    ) : SingleSceneNavigator<Navigator.Events>(savedState) {

        override fun createScene(state: SceneState?): Scene<out Container> {
            return state?.let { TestScene.create(it) } ?: scene
        }
    }
}