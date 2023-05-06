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

package com.nhaarman.acorn.navigation

import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SavedState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.reflect.KClass

internal class CompositeStackNavigatorTest {

    private val navigator1Scene1 = spy(SavableTestScene(11))
    private val navigator2Scene1 = spy(SavableTestScene(21))
    private val navigator2Scene2 = spy(SavableTestScene(22))
    private val navigator3Scene1 = spy(SavableTestScene(31))

    private val navigator1 = spy(TestSingleSceneNavigator(navigator1Scene1))
    private val navigator2 = spy(SavableTestStackNavigator(listOf(navigator2Scene1)))
    private val navigator3 = spy(SavableTestStackNavigator(listOf(navigator3Scene1)))

    private val navigator = TestCompositeStackNavigator(listOf(navigator1))
    private val listener = mock<Navigator.Events>()

    @Nested
    inner class NavigatorStates {

        @Nested
        inner class InactiveNavigator {

            @Test
            fun `navigator is not finished`() {
                // When
                navigator.addNavigatorEventsListener(listener)

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `navigator is not destroyed`() {
                // Then
                expect(navigator.isDestroyed()).toBe(false)
            }

            @Test
            fun `added listener does not get notified of scene`() {
                // When
                navigator.addNavigatorEventsListener(listener)

                // Then
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `pushing a navigator does not notify listeners of scene`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.push(navigator2)

                // Then
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `popping the last scene and navigator from the stack notifies listeners of finished`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.pop()

                // Then
                verify(listener).finished()
            }

            @Test
            fun `popping the second to last navigator from the stack does not notify listeners of finished`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                navigator.pop()

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `popping the second to last navigator from the navigator does not notify listeners of scenes`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                navigator.pop()

                // Then
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `onBackPressed for a single scene notifies listeners of finished`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(true)
                verify(listener).finished()
            }

            @Test
            fun `finish notifies listeners of finished`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.finish()

                // Then
                verify(listener).finished()
            }

            @Test
            fun `onBackPressed for a single scene does not notify screen`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(true)
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `onBackPressed for multiple navigators does not notify screen`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(true)
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `onBackPressed after navigator is destroyed does not notify listeners`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()
                navigator.onDestroy()

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(false)
                verify(listener, never()).finished()
            }

            @Test
            fun `finish after navigator is destroyed does not notify listeners`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()
                navigator.onDestroy()

                // When
                navigator.finish()

                // Then
                verify(listener, never()).finished()
            }
        }

        @Nested
        inner class ActiveNavigator {

            @Test
            fun `navigator is not finished`() {
                // Given
                navigator.onStart()

                // When
                navigator.addNavigatorEventsListener(listener)

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `navigator is not destroyed`() {
                // Given
                navigator.onStart()

                // Then
                expect(navigator.isDestroyed()).toBe(false)
            }

            @Test
            fun `added listener gets notified of scene`() {
                // Given
                navigator.onStart()

                // When
                navigator.addNavigatorEventsListener(listener)

                // Then
                verify(listener).scene(navigator1Scene1, null)
            }

            @Test
            fun `removed listener does not get notified of scene`() {
                // Given
                val disposable = navigator.addNavigatorEventsListener(listener)
                disposable.dispose()

                // When
                navigator.onStart()

                // Then
                verify(listener, never()).scene(any(), anyOrNull())
            }

            @Test
            fun `non disposed listener is not disposed`() {
                // Given
                val disposable = navigator.addNavigatorEventsListener(listener)

                // Then
                expect(disposable.isDisposed()).toBe(false)
            }

            @Test
            fun `disposed listener is disposed`() {
                // Given
                val disposable = navigator.addNavigatorEventsListener(listener)

                // When
                disposable.dispose()

                // Then
                expect(disposable.isDisposed()).toBe(true)
            }

            @Test
            fun `starting navigator notifies listeners of scene`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.onStart()

                // Then
                verify(listener).scene(navigator1Scene1, null)
            }

            @Test
            fun `starting navigator multiple times notifies listeners of scene only once`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.onStart()
                navigator.onStart()

                // Then
                verify(listener, times(1)).scene(any(), anyOrNull())
            }

            @Test
            fun `starting navigator second time in a callback only notifies listener once`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.addNavigatorEventsListener(object : Navigator.Events {
                    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                        navigator.onStart()
                    }

                    override fun finished() {
                    }
                })
                navigator.onStart()

                // Then
                verify(listener, times(1)).scene(any(), anyOrNull())
            }

            @Test
            fun `starting navigator does not finish`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.onStart()

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `pushing a navigator notifies listeners of scene`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                // When
                navigator.push(navigator2)

                // Then
                verify(listener).scene(eq(navigator2Scene1), anyOrNull())
            }

            @Test
            fun `pushing a navigator - scene notification has forward transition data`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                // When
                navigator.push(navigator2)

                // Then
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())

                    expect(lastValue.isBackwards).toBe(false)
                }
            }

            @Test
            fun `replacing a navigator - scene notification has forward transition data`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                // When
                navigator.replace(navigator2)

                // Then
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())

                    expect(lastValue.isBackwards).toBe(false)
                }
            }

            @Test
            fun `start navigator after navigator push notifies pushed scene`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                navigator.onStart()

                // Then
                verify(listener).scene(navigator2Scene1)
            }

            @Test
            fun `popping the last scene and navigator from the navigator notifies listeners of finished`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                // When
                navigator.pop()

                // Then
                verify(listener).finished()
            }

            @Test
            fun `popping the second to last navigator from the stack does not notify listeners of finished`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()
                navigator.push(navigator2)

                // When
                navigator.pop()

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `popping the second to last navigator from the stack notifies listeners of proper scenes`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()
                navigator.push(navigator2)

                // When
                navigator.pop()

                // Then
                listener.inOrder {
                    verify().scene(eq(navigator1Scene1), anyOrNull())
                    verify().scene(eq(navigator2Scene1), anyOrNull())
                    verify().scene(eq(navigator1Scene1), anyOrNull())
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `popping the second to last navigator from the stack scene - notification has backward transition data`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()
                navigator.push(navigator2)

                // When
                navigator.pop()

                // Then
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())
                    expect(lastValue.isBackwards).toBe(true)
                }
            }

            @Test
            fun `onBackPressed for a single scene notifies finished`() {
                // Given
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener)

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(true)
                verify(listener).finished()
            }

            @Test
            fun `onBackPressed for a single scene does not notify screen`() {
                // Given
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener)

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(true)
                listener.inOrder {
                    verify().scene(navigator1Scene1)
                    verify().finished()
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `onBackPressed for multiple navigators notifies proper scenes`() {
                // Given
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                val result = navigator.onBackPressed()

                // Then
                expect(result).toBe(true)
                listener.inOrder {
                    verify().scene(eq(navigator1Scene1), anyOrNull())
                    verify().scene(eq(navigator2Scene1), anyOrNull())
                    verify().scene(eq(navigator1Scene1), anyOrNull())
                    verifyNoMoreInteractions()
                }
            }

            @Test
            fun `onBackPressed for multiple navigators - scene notification has backward transition data`() {
                // Given
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                navigator.onBackPressed()

                // Then
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())
                    expect(lastValue.isBackwards).toBe(true)
                }
            }

            @Test
            fun `forwards from nested navigator is propagated`() {
                // Given
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)

                // When
                navigator2.push(navigator2Scene2)

                // Then
                // Then
                argumentCaptor<TransitionData> {
                    verify(listener, atLeastOnce()).scene(any(), capture())
                    expect(lastValue.isBackwards).toBe(false)
                }
            }

            @Test
            fun `backwards from nested navigator is propagated`() {
                // Given
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener)
                navigator.push(navigator2)
                navigator2.push(navigator2Scene2)

                // When
                navigator.onBackPressed()

                // Then
                // Then
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
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.onStop()

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `stopped navigator is not destroyed`() {
                // When
                navigator.onStop()

                // Then
                expect(navigator.isDestroyed()).toBe(false)
            }
        }

        @Nested
        inner class DestroyedNavigator {

            @Test
            fun `destroying navigator does not finish`() {
                // Given
                navigator.addNavigatorEventsListener(listener)

                // When
                navigator.onDestroy()

                // Then
                verify(listener, never()).finished()
            }

            @Test
            fun `isDestroyed returns true`() {
                // When
                navigator.onDestroy()

                // Then
                expect(navigator.isDestroyed()).toBe(true)
            }

            @Test
            fun `pushing a scene for destroyed navigator does not notify listeners`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onDestroy()

                // When
                navigator.push(navigator2)

                // Then
                verify(listener, never()).scene(any(), any())
            }

            @Test
            fun `popping from multi item stack for destroyed navigator does not notify scene`() {
                // Given
                navigator.addNavigatorEventsListener(listener)
                navigator.onDestroy()
                navigator.push(navigator2)

                // When
                navigator.pop()

                // Then
                verify(listener, never()).scene(any(), any())
            }
        }
    }

    @Nested
    inner class NavigatorInteractionForSingleChildNavigatorStack {

        @Test
        fun `starting navigator starts child navigator`() {
            // When
            navigator.onStart()

            // Then
            navigator1.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator multiple times starts child navigator only once`() {
            // When
            navigator.onStart()
            navigator.onStart()

            // Then
            verify(navigator1, times(1)).onStart()
        }

        @Test
        fun `stopping an inactive navigator does not stop child navigator`() {
            // When
            navigator.onStop()

            // Then
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `stopping an active navigator stops child navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.onStop()

            // Then
            navigator1.inOrder {
                verify().onStart()
                verify().onStop()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop child navigator`() {
            // When
            navigator.onDestroy()

            // Then
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy child navigator`() {
            // When
            navigator.onDestroy()

            // Then
            verify(navigator1).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys child navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.onDestroy()

            // Then
            navigator1.inOrder {
                verify().onStart()
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start child navigator`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStart()

            // Then
            verify(navigator1).onDestroy()
            verify(navigator1, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start child navigator`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStop()

            // Then
            verify(navigator1).onDestroy()
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys child navigator once`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onDestroy()

            // Then
            verify(navigator1, times(1)).onDestroy()
        }
    }

    @Nested
    inner class NavigatorInteractionForMultiChildNavigatorStack {

        private val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

        @Test
        fun `starting navigator starts top child navigator`() {
            // When
            navigator.onStart()

            // Then
            navigator2.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator does not start bottom child navigators`() {
            // When
            navigator.onStart()

            // Then
            verify(navigator1, never()).onStart()
        }

        @Test
        fun `starting navigator multiple times starts child navigator only once`() {
            // When
            navigator.onStart()
            navigator.onStart()

            // Then
            verify(navigator2, times(1)).onStart()
        }

        @Test
        fun `stopping an inactive navigator does not stop child navigator`() {
            // When
            navigator.onStop()

            // Then
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `stopping an active navigator stops child navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.onStop()

            // Then
            navigator2.inOrder {
                verify().onStart()
                verify().onStop()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop child navigators`() {
            // When
            navigator.onDestroy()

            // Then
            verify(navigator1, never()).onStop()
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy child navigators`() {
            // When
            navigator.onDestroy()

            // Then
            inOrder(navigator1, navigator2) {
                verify(navigator2).onDestroy()
                verify(navigator1).onDestroy()
            }
        }

        @Test
        fun `destroy an active navigator stops top child navigator and destroys all child navigators`() {
            // Given
            navigator.onStart()

            // When
            navigator.onDestroy()

            // Then
            inOrder(navigator1, navigator2) {
                verify(navigator2).onStart()
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator1).onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start child navigator`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStart()

            // Then
            verify(navigator2).onDestroy()
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start child scene`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStop()

            // Then
            verify(navigator2).onDestroy()
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys child navigator once`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onDestroy()

            // Then
            verify(navigator2, times(1)).onDestroy()
        }
    }

    @Nested
    inner class NavigatorInteractionWhenManipulatingStack {

        @Test
        fun `popping from a single item stack for inactive navigator destroys child navigator`() {
            // When
            navigator.pop()

            // When
            verify(navigator1).onDestroy()
        }

        @Test
        fun `popping from a single item stack for inactive navigator destroys parent navigator`() {
            // When
            navigator.pop()

            // When
            expect(navigator.isDestroyed()).toBe(true)
        }

        @Test
        fun `popping from a single item stack for inactive navigator does not stop child navigator`() {
            // When
            navigator.pop()

            // When
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `popping from a multi item stack for inactive navigator destroys latest child navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

            // When
            navigator.pop()

            // When
            verify(navigator2).onDestroy()
        }

        @Test
        fun `popping from a single item stack for active navigator stops and destroys child navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.pop()

            // When
            navigator1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `popping from a single item stack for active navigator destroys parent navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.pop()

            // When
            expect(navigator.isDestroyed()).toBe(true)
        }

        @Test
        fun `popping from a multi item stack for active navigator stops and destroys latest child navigator, and starts current child navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            // When
            navigator.pop()

            // When
            inOrder(navigator1, navigator2) {
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator1).onStart()
            }
        }

        @Test
        fun `replacing top item from a single item stack for inactive navigator destroys original navigator`() {
            // When
            navigator.replace(navigator2)

            // When
            verify(navigator1).onDestroy()
        }

        @Test
        fun `replacing top item from a single item stack for inactive navigator does not stop original navigator`() {
            // When
            navigator.replace(navigator2)

            // When
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `replacing top item from a multi item stack for inactive navigator destroys latest navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

            // When
            navigator.replace(navigator3)

            // When
            verify(navigator2).onDestroy()
        }

        @Test
        fun `replacing top item from a multi item stack for inactive navigator does not start replacing navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

            // When
            navigator.replace(navigator3)

            // When
            verify(navigator3, never()).onStart()
        }

        @Test
        fun `replacing top item from a single item stack for active navigator stops and destroys original navigator, and starts replacing navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.replace(navigator2)

            // When
            inOrder(navigator1, navigator2) {
                verify(navigator1).onStop()
                verify(navigator1).onDestroy()
                verify(navigator2).onStart()
            }
        }

        @Test
        fun `replacing top item from a multi item stack for active navigator stops and destroys latest navigator, and starts replacing navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            // When
            navigator.replace(navigator3)

            // When
            inOrder(navigator1, navigator2, navigator3) {
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator3).onStart()
            }
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator destroys child navigator`() {
            // When
            navigator.onBackPressed()

            // When
            verify(navigator1).onDestroy()
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator destroys parent navigator`() {
            // When
            navigator.onBackPressed()

            // When
            expect(navigator.isDestroyed()).toBe(true)
        }

        @Test
        fun `onBackPressed from a single item stack for inactive navigator does not stop child navigator`() {
            // When
            navigator.onBackPressed()

            // When
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `onBackPressed from a multi item stack for inactive navigator destroys latest child navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))

            // When
            navigator.onBackPressed()

            // When
            verify(navigator2).onDestroy()
        }

        @Test
        fun `onBackPressed from a single item stack for active navigator stops and destroys child navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.onBackPressed()

            // When
            navigator1.inOrder {
                verify().onStop()
                verify().onDestroy()
            }
        }

        @Test
        fun `onBackPressed from a single item stack for active navigator destroys parent navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.onBackPressed()

            // When
            expect(navigator.isDestroyed()).toBe(true)
        }

        @Test
        fun `onBackPressed from a multi item stack for active navigator stops and destroys latest child navigator, and starts current child navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            // When
            navigator.onBackPressed()

            // When
            inOrder(navigator1, navigator2) {
                verify(navigator2).onStop()
                verify(navigator2).onDestroy()
                verify(navigator1).onStart()
            }
        }

        @Test
        fun `onBackPressed from a multi item stack for active navigator with multiple scenes does not stop or destroy top child navigator`() {
            // Given
            val navigator = TestCompositeStackNavigator(listOf(navigator1, navigator2))
            navigator.onStart()

            // When
            navigator2.push(navigator2Scene2)
            navigator.onBackPressed()

            // When
            verify(navigator2, never()).onStop()
        }

        @Test
        fun `pushing for inactive navigator does not stop previous child navigator`() {
            // When
            navigator.push(navigator2)

            // Then
            verify(navigator1, never()).onStop()
        }

        @Test
        fun `pushing for inactive navigator does not start pushed navigator`() {
            // When
            navigator.push(navigator2)

            // Then
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `pushing for destroyed navigator does not start pushed navigator`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.push(navigator2)

            // Then
            verify(navigator2, never()).onStart()
        }

        @Test
        fun `pushing for started navigator stops previous child navigator and starts pushed child navigator`() {
            // Given
            navigator.onStart()

            // When
            navigator.push(navigator2)

            // Then
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

        private val navigator1Scene1 = SavableTestScene(11)
        private val navigator2Scene1 = SavableTestScene(21)
        private val navigator3Scene1 = SavableTestScene(31)

        private val navigator1 = TestSingleSceneNavigator(navigator1Scene1)
        private val navigator2 = TestSingleSceneNavigator(navigator2Scene1)
        private val savableNavigator1 = SavableTestSingleSceneNavigator(navigator1Scene1)
        private val savableNavigator2 = SavableTestStackNavigator(listOf(navigator2Scene1))
        private val savableNavigator3 = SavableTestStackNavigator(listOf(navigator3Scene1))

        private val navigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), null)

        @Test
        fun `CompositeStackNavigator does not implement SavableNavigator by default`() {
            // Given
            val navigator: Navigator = TestCompositeStackNavigator(listOf(savableNavigator1))

            // Then
            expect(navigator is SavableNavigator).toBe(false)
        }

        @Test
        fun `saving and restoring state for single savable navigator stack`() {
            // Given
            val navigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), null)
            navigator.onStart()
            navigator1Scene1.foo = 3

            // When
            val bundle = navigator.saveInstanceState()
            navigator1Scene1.foo = 42
            val restoredNavigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture(), anyOrNull())
                expect(lastValue).toNotBeTheSameAs(navigator1Scene1)
                expect(lastValue).toBeInstanceOf<SavableTestScene> {
                    expect(it.foo).toBe(3)
                }
            }
        }

        @Test
        fun `saving and restoring state for single non savable navigator stack`() {
            // Given
            val navigator = SavableTestCompositeStackNavigator(listOf(navigator1), null)
            navigator.onStart()
            navigator1Scene1.foo = 3

            // When
            val bundle = navigator.saveInstanceState()
            navigator1Scene1.foo = 42
            val restoredNavigator = SavableTestCompositeStackNavigator(listOf(savableNavigator2), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture(), anyOrNull())
                expect(lastValue).toBeInstanceOf<SavableTestScene> {
                    expect(it.foo).toBe(21)
                }
            }
        }

        @Test
        fun `saving and restoring state for multiple savable child navigators on navigator stack`() {
            // Given
            val navigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), null)
            navigator.onStart()
            navigator1Scene1.foo = 3
            navigator.push(savableNavigator2)
            navigator2Scene1.foo = 4

            // When
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture(), anyOrNull())
                expect(lastValue).toNotBeTheSameAs(navigator2Scene1)
                expect(lastValue).toBeInstanceOf<SavableTestScene> {
                    expect(it.foo).toBe(4)
                }
            }

            // When
            restoredNavigator.pop()

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener, times(2)).scene(capture(), anyOrNull())
                expect(lastValue).toNotBeTheSameAs(navigator1Scene1)
                expect(lastValue).toBeInstanceOf<SavableTestScene> {
                    expect(it.foo).toBe(3)
                }
            }
        }

        @Test
        fun `saving and restoring state for mixed savable and non savable navigator stack`() {
            // Given
            val navigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), null)
            navigator.onStart()
            navigator1Scene1.foo = 3
            navigator.push(navigator2)
            navigator2Scene1.foo = 4

            // When
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableTestCompositeStackNavigator(listOf(savableNavigator2), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture(), anyOrNull())
                expect(lastValue).toNotBeTheSameAs(navigator2Scene1)
                expect(lastValue).toBeInstanceOf<SavableTestScene> {
                    expect(it.foo).toBe(3)
                }
            }

            // When
            restoredNavigator.pop()

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).finished()
            }
        }

        @Test
        fun `saving and restoring state for mixed savable and non savable navigator stack 2`() {
            // Given
            val navigator = SavableTestCompositeStackNavigator(listOf(savableNavigator1), null)
            navigator.onStart()
            navigator1Scene1.foo = 3
            navigator.push(navigator2)
            navigator2Scene1.foo = 4
            navigator.push(savableNavigator3)
            navigator3Scene1.foo = 5

            // When
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableTestCompositeStackNavigator(listOf(savableNavigator2), bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).scene(capture(), anyOrNull())
                expect(lastValue).toNotBeTheSameAs(navigator2Scene1)
                expect(lastValue).toBeInstanceOf<SavableTestScene> {
                    expect(it.foo).toBe(3)
                }
            }

            // When
            restoredNavigator.pop()

            // Then
            argumentCaptor<Scene<out Container>> {
                verify(listener).finished()
            }
        }

        @Test
        fun `saved state from callback is the same as saved state _after_ callback`() {
            // Given
            var state1: SavedState? = null
            navigator.addNavigatorEventsListener(object : Navigator.Events {
                override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                    state1 = navigator.saveInstanceState()
                }

                override fun finished() {
                }
            })
            navigator.onStart()

            // When
            navigator.push(savableNavigator2)
            val state2 = navigator.saveInstanceState()

            // Then
            expect(state1).toBe(state2)
        }
    }

    class TestCompositeStackNavigator(
        private val initialStack: List<Navigator>,
    ) : CompositeStackNavigator(null) {

        override fun initialStack(): List<Navigator> {
            return initialStack
        }

        override fun instantiateNavigator(
            navigatorClass: KClass<out Navigator>,
            state: NavigatorState?,
        ): Navigator {
            error("Not supported")
        }
    }

    open class TestSingleSceneNavigator(
        private val scene: Scene<out Container>,
    ) : SingleSceneNavigator(null) {

        override fun createScene(state: SceneState?): Scene<out Container> {
            return scene
        }
    }

    open class SavableTestStackNavigator(
        private val initialStack: List<SavableTestScene>,
        savedState: NavigatorState? = null,
    ) : StackNavigator(savedState), SavableNavigator {

        override fun initialStack(): List<Scene<out Container>> {
            return initialStack
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<*> {
            return when (sceneClass) {
                SavableTestScene::class -> SavableTestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    class SavableTestCompositeStackNavigator(
        private val initialStack: List<Navigator>,
        savedState: NavigatorState?,
    ) : CompositeStackNavigator(savedState), SavableNavigator {

        override fun initialStack(): List<Navigator> {
            return initialStack
        }

        override fun instantiateNavigator(
            navigatorClass: KClass<out Navigator>,
            state: NavigatorState?,
        ): Navigator {
            return when (navigatorClass) {
                SavableTestSingleSceneNavigator::class -> SavableTestSingleSceneNavigator(
                    SavableTestScene(0),
                    state,
                )
                SavableTestStackNavigator::class -> SavableTestStackNavigator(emptyList(), state)
                else -> error("Unknown navigator class: $navigatorClass.")
            }
        }
    }

    open class SavableTestSingleSceneNavigator(
        private val scene: Scene<out Container>,
        savedState: NavigatorState? = null,
    ) : SingleSceneNavigator(savedState), SavableNavigator {

        override fun createScene(state: SceneState?): Scene<out Container> {
            return state?.let { SavableTestScene.create(it) } ?: scene
        }
    }
}
