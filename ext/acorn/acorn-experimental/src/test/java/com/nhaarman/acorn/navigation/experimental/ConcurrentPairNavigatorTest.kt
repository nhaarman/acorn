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

package com.nhaarman.acorn.navigation.experimental

import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.SavableNavigator
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SavedState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

@ExperimentalConcurrentPairNavigator
internal class ConcurrentPairNavigatorTest {

    private val scene1 = spy(SavableTestScene(1))
    private val scene2 = spy(SavableTestScene(2))
    private val scene3 = spy(SavableTestScene(3))

    private val navigator = TestConcurrentPairNavigator(scene1)
    private val listener = spy(TestListener())

    @Nested
    inner class NavigatorStates {

        @Test
        fun `non disposed listener is not disposed`() {
            /* Given */
            val disposable = navigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(disposable.isDisposed()).toBe(false)
        }

        @Test
        fun `disposed listener is disposed`() {
            /* Given */
            val disposable = navigator.addNavigatorEventsListener(listener)

            /* When */
            disposable.dispose()

            /* Then */
            expect(disposable.isDisposed()).toBe(true)
        }

        @Nested
        inner class InactiveNavigator {

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
            fun `inactive navigator does not notify newly added listener of scene`() {
                /* When */
                navigator.addNavigatorEventsListener(listener)

                /* Then */
                expect(listener.lastScene).toBeNull()
            }

            @Test
            fun `stopping inactive navigator does not finish`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)

                /* When */
                navigator.onStop()

                /* Then */
                expect(listener.finished).toBe(false)
            }

            @Test
            fun `destroying inactive navigator does not finish`() {
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
            fun `popping for only base scene for inactive navigator does not notify finished`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)

                /* When */
                navigator.pop()

                /* Then */
                expect(listener.finished).toBe(false)
            }

            @Test
            fun `popping for base and second scene for inactive navigator does not notify finished`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)
                navigator.push(scene2)

                /* When */
                navigator.pop()

                /* Then */
                expect(listener.finished).toBe(false)
            }

            @Test
            fun `popping for base and second scene for inactive navigator does not notify scene`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)
                navigator.push(scene2)

                /* When */
                navigator.pop()

                /* Then */
                expect(listener.lastScene).toBeNull()
            }

            @Test
            fun `finish for inactive navigator notifies listeners of finished`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)

                /* When */
                navigator.finish()

                /* Then */
                expect(listener.finished).toBe(true)
            }

            @Test
            fun `onBackPressed for only base scene for inactive navigator notifies listeners of finished`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                expect(listener.finished).toBe(true)
            }

            @Test
            fun `onBackPressed for base and second scene for inactive navigator does not notify listeners of finished`() {
                /* Given */
                navigator.push(scene2)
                navigator.addNavigatorEventsListener(listener)

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                expect(listener.finished).toBe(false)
            }
        }

        @Nested
        inner class StartingNavigator {

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
            fun `starting navigator does not notify removed listeners of scene`() {
                /* Given */
                val disposable = navigator.addNavigatorEventsListener(listener)
                disposable.dispose()

                /* When */
                navigator.onStart()

                /* Then */
                verify(listener, never()).scene(any(), anyOrNull())
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
            fun `starting navigator second time in a callback only notifies listener once`() {
                /* Given */
                val listener = mock<Navigator.Events>()
                navigator.addNavigatorEventsListener(listener)

                /* When */
                navigator.addNavigatorEventsListener(object : Navigator.Events {
                    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                        navigator.onStart()
                    }

                    override fun finished() {
                    }
                })
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
            fun `start navigator after scene push notifies combined scene`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)
                navigator.push(scene2)

                /* When */
                navigator.onStart()

                /* Then */
                expect(listener.lastScene).toBeInstanceOf<CombinedScene> {
                    expect(it.key).toBe(scene2.key)
                    expect(it.firstScene).toBe(scene1)
                    expect(it.secondScene).toBe(scene2)
                }
            }
        }

        @Nested
        inner class ActiveNavigator {

            @Test
            fun `active navigator is not destroyed`() {
                /* Given */
                navigator.onStart()

                /* Then */
                expect(navigator.isDestroyed()).toBe(false)
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
            fun `active navigator with second scene does notify newly added listener of combined scene`() {
                /* Given */
                navigator.push(scene2)
                navigator.onStart()

                /* When */
                navigator.addNavigatorEventsListener(listener)

                /* Then */
                expect(listener.lastScene).toBeInstanceOf<CombinedScene> {
                    expect(it.key).toBe(scene2.key)
                    expect(it.firstScene).toBe(scene1)
                    expect(it.secondScene).toBe(scene2)
                }
            }

            @Test
            fun `pushing a scene for active navigator does notify listeners`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                /* When */
                navigator.push(scene2)

                /* Then */
                expect(listener.lastScene).toBeInstanceOf<CombinedScene> {
                    expect(it.key).toBe(scene2.key)
                    expect(it.firstScene).toBe(scene1)
                    expect(it.secondScene).toBe(scene2)
                }
            }

            @Test
            fun `pushing a scene for active navigator does not notify removed listeners`() {
                /* Given */
                navigator.onStart()
                navigator.addNavigatorEventsListener(listener).dispose()

                /* When */
                navigator.push(scene2)

                /* Then */
                verify(listener, never()).scene(eq(scene2), anyOrNull())
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
            fun `popping for only base scene for active navigator does not notify finished`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                /* When */
                navigator.pop()

                /* Then */
                expect(listener.finished).toBe(false)
            }

            @Test
            fun `popping for base and second scene for active navigator does not notify finished`() {
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
            fun `popping for base and second scene for active navigator notifies proper scene`() {
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
            fun `popping for base and second scene for active navigator - scene notification has backward transition data`() {
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
            fun `onBackPressed for only base scene for active navigator notifies listeners of finished`() {
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
            fun `onBackPressed for base and second scene for active navigator does not notify listeners of finished`() {
                /* Given */
                navigator.push(scene2)
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                /* When */
                val result = navigator.onBackPressed()

                /* Then */
                expect(result).toBe(true)
                expect(listener.finished).toBe(false)
            }

            @Test
            fun `onBackPressed for base and second scene for active navigator notifies listener of base scene`() {
                /* Given */
                navigator.push(scene2)
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                /* When */
                navigator.onBackPressed()

                /* Then */
                expect(listener.lastScene).toBe(scene1)
            }

            @Test
            fun `onBackPressed for only base scene for active navigator results in destroyed navigator`() {
                /* Given */
                navigator.onStart()

                /* When */
                navigator.onBackPressed()

                /* Then */
                expect(navigator.isDestroyed()).toBe(true)
            }

            @Test
            fun `finish for active navigator notifies listeners of finished`() {
                /* Given */
                navigator.addNavigatorEventsListener(listener)
                navigator.onStart()

                /* When */
                navigator.finish()

                /* Then */
                expect(listener.finished).toBe(true)
            }

            @Test
            fun `finish for active navigator results in destroyed navigator`() {
                /* Given */
                navigator.onStart()

                /* When */
                navigator.finish()

                /* Then */
                expect(navigator.isDestroyed()).toBe(true)
            }
        }

        @Nested
        inner class StoppedNavigator {

            @Test
            fun `stopped navigator is not destroyed`() {
                /* Given */
                navigator.onStart()

                /* When */
                navigator.onStop()

                /* Then */
                expect(navigator.isDestroyed()).toBe(false)
            }
        }

        @Nested
        inner class DestroyedNavigator {

            @Test
            fun `destroyed navigator is destroyed`() {
                /* When */
                navigator.onDestroy()

                /* Then */
                expect(navigator.isDestroyed()).toBe(true)
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
            fun `popping for base and second scene for destroyed navigator does not notify scene`() {
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
            fun `onBackPressed for only base scene after navigator is destroyed does not notify listeners`() {
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
    }

    @Nested
    inner class SceneInteractionForOnlyBaseScene {

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
        fun `finishing an inactive navigator does not stop Scene`() {
            /* When */
            navigator.finish()

            /* Then */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `finishing an inactive navigator does destroy Scene`() {
            /* When */
            navigator.finish()

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
        fun `finishing an active navigator stops and destroys Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.finish()

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
    inner class SceneInteractionForBaseAndSecondScenes {

        @BeforeEach
        fun before() {
            /* Given */
            navigator.push(scene2)
        }

        @Test
        fun `starting navigator starts both scene`() {
            /* When */
            navigator.onStart()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene2).onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator multiple times starts Scene only once`() {
            /* When */
            navigator.onStart()
            navigator.onStart()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene2).onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop scenes`() {
            /* When */
            navigator.onStop()

            /* Then */
            verifyNoMoreInteractions(scene1)
            verifyNoMoreInteractions(scene2)
        }

        @Test
        fun `stopping an active navigator stops both scenes`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene1).onStop()
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
        fun `finishing an inactive navigator does not stop Scenes`() {
            /* When */
            navigator.finish()

            /* Then */
            verify(scene1, never()).onStop()
            verify(scene2, never()).onStop()
        }

        @Test
        fun `finishing an inactive navigator does destroy Scene`() {
            /* When */
            navigator.finish()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
            }
        }

        @Test
        fun `destroy an active navigator stops both scenes and destroys both Scenes`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene1).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `finishing an active navigator stops both scenes and destroys both Scenes`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.finish()

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene2).onStart()
                verify(scene2).onStop()
                verify(scene1).onStop()
                verify(scene2).onDestroy()
                verify(scene1).onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start scenes`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStart()

            /* Then */
            verify(scene1).onDestroy()
            verify(scene2).onDestroy()
            verify(scene1, never()).onStart()
            verify(scene2, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not stop scenes`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(scene1).onDestroy()
            verify(scene2).onDestroy()
            verify(scene1, never()).onStop()
            verify(scene2, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys scenes once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(scene1, times(1)).onDestroy()
            verify(scene2, times(1)).onDestroy()
        }
    }

    @Nested
    inner class SceneInteractionWhenManipulatingStack {

        @Test
        fun `popping for only base scene for inactive navigator does not destroy scene`() {
            /* When */
            navigator.pop()

            /* When */
            verify(scene1, never()).onDestroy()
        }

        @Test
        fun `popping for only base scene for inactive navigator does not stop scene`() {
            /* When */
            navigator.pop()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `popping for base and second scene for inactive navigator destroys second scene`() {
            /* Given */
            navigator.push(scene2)

            /* When */
            navigator.pop()

            /* When */
            verify(scene2).onDestroy()
        }

        @Test
        fun `popping for only base scene for active navigator does not stop scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `popping for only base scene for active navigator does not destroy scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            verify(scene1, never()).onDestroy()
        }

        @Test
        fun `popping for base and second scene for active navigator stops and destroys latest scene`() {
            /* Given */
            navigator.push(scene2)
            navigator.onStart()

            /* When */
            navigator.pop()

            /* When */
            inOrder(scene2) {
                verify(scene2).onStop()
                verify(scene2).onDestroy()
            }
        }

        @Test
        fun `pushing a new scene for base and second scene for inactive navigator destroys original second scene`() {
            /* Given */
            navigator.push(scene2)

            /* When */
            navigator.push(scene3)

            /* When */
            verify(scene2).onDestroy()
        }

        @Test
        fun `pushing a new scene for base and second scene for inactive navigator does not start newly pushed scene`() {
            /* Given */
            navigator.push(scene2)

            /* When */
            navigator.push(scene3)

            /* When */
            verifyNoMoreInteractions(scene3)
        }

        @Test
        fun `pushing a new scene for base and second scene for active navigator stops and destroys latest scene, and starts newly pushed scene`() {
            /* Given */
            navigator.push(scene2)
            navigator.onStart()

            /* When */
            navigator.push(scene3)

            /* When */
            inOrder(scene1, scene2, scene3) {
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene3).onStart()
            }
        }

        @Test
        fun `onBackPressed for only base scene for inactive navigator destroys scene`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene1).onDestroy()
        }

        @Test
        fun `onBackPressed for only base scene for inactive navigator does not stop scene`() {
            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `onBackPressed for base and second scene for inactive navigator destroys second scene`() {
            /* Given */
            navigator.push(scene2)

            /* When */
            navigator.onBackPressed()

            /* When */
            verify(scene2).onDestroy()
        }

        @Test
        fun `onBackPressed for only base scene for active navigator stops and destroys scene`() {
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
        fun `onBackPressed for base and second scene for active navigator stops and destroys second scene, and does not start current scene`() {
            /* Given */
            navigator.push(scene2)
            navigator.onStart()

            /* When */
            navigator.onBackPressed()

            /* When */
            inOrder(scene1, scene2) {
                verify(scene2).onStop()
                verify(scene2).onDestroy()
                verify(scene1, never()).onStart()
            }
        }

        @Test
        fun `pushing for inactive navigator does not stop base scene`() {
            /* When */
            navigator.push(scene2)

            /* Then */
            verify(scene1, never()).onStop()
        }

        @Test
        fun `pushing for inactive navigator does not start pushed scene`() {
            /* When */
            navigator.push(scene2)

            /* Then */
            verify(scene2, never()).onStart()
        }

        @Test
        fun `pushing for destroyed navigator does not start pushed scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.push(scene2)

            /* Then */
            verify(scene2, never()).onStart()
        }

        @Test
        fun `pushing for started navigator does not stop base scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.push(scene2)

            /* Then */
            inOrder(scene1) {
                verify(scene1).onStart()
                verify(scene1, never()).onStop()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `pushing for started navigator starts pushed scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.push(scene2)

            /* Then */
            inOrder(scene1, scene2) {
                verify(scene1).onStart()
                verify(scene2).onStart()
                verifyNoMoreInteractions()
            }
        }
    }

    @Nested
    inner class SavingState {

        private val scene1 = TestScene(1)
        private val scene2 = TestScene(2)
        private val savableScene1 = SavableTestScene(3)
        private val savableScene2 = SavableTestScene(4)
        private val savableScene3 = SavableTestScene(5)
        private val savableScene4 = SavableTestScene(6)

        private val navigator = TestConcurrentPairNavigator(savableScene1)

        @Test
        fun `ConcurrentPairNavigator does not implement SavableNavigator by default`() {
            /* Given */
            val navigator: Navigator = TestConcurrentPairNavigator(savableScene1)

            /* Then */
            expect(navigator is SavableNavigator).toBe(false)
        }

        @Test
        fun `saving and restoring state for only savable base scene`() {
            /* Given */
            val navigator = TestConcurrentPairNavigator(savableScene1)
            navigator.onStart()
            savableScene1.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()
            savableScene1.foo = 6

            val restoredNavigator = SavableTestConcurrentPairNavigator(savableScene1, bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBeInstanceOf<SavableTestScene> {
                expect(it.foo).toBe(3)
            }
        }

        @Test
        fun `saving and restoring state for only non savable base scene`() {
            /* Given */
            val navigator = TestConcurrentPairNavigator(scene1)
            navigator.onStart()

            /* When */
            val bundle = navigator.saveInstanceState()

            val restoredNavigator = SavableTestConcurrentPairNavigator(scene2, bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `saving and restoring state for base and second scenes`() {
            /* Given */
            navigator.onStart()
            savableScene1.foo = 3
            savableScene2.foo = 42
            navigator.push(savableScene2)

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableTestConcurrentPairNavigator(savableScene3, bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            savableScene1.foo = 1
            savableScene2.foo = 2

            /* Then */
            expect(listener.lastScene).toBeInstanceOf<CombinedScene> {
                expect((it.firstScene as SavableTestScene).foo).toBe(3)
                expect((it.secondScene as SavableTestScene).foo).toBe(42)
            }
        }

        @Test
        fun `saving and restoring state for base and non savable second scenes`() {
            /* Given */
            val navigator = SavableTestConcurrentPairNavigator(savableScene1)
            navigator.onStart()
            savableScene1.foo = 3
            scene2.foo = 42
            navigator.push(scene2)

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableTestConcurrentPairNavigator(savableScene2, bundle)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(savableScene1)
        }

        @Test
        fun `restoring from empty state ignores state`() {
            /* When */
            val result = SavableTestConcurrentPairNavigator(
                savableScene1,
                NavigatorState()
            )
            result.onStart()
            result.addNavigatorEventsListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(savableScene1)
        }

        @Test
        fun `saved state from callback is the same as saved state _after_ callback`() {
            /* Given */
            var state1: SavedState? = null
            navigator.addNavigatorEventsListener(object : Navigator.Events {
                override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                    state1 = navigator.saveInstanceState()
                }

                override fun finished() {
                }
            })
            navigator.onStart()

            /* When */
            navigator.push(savableScene2)
            val state2 = navigator.saveInstanceState()

            /* Then */
            expect(state1).toBe(state2)
        }
    }

    class TestConcurrentPairNavigator(
        private val initialScene: Scene<*>
    ) : ConcurrentPairNavigator(null) {

        override fun createInitialScene(): Scene<out Container> {
            return initialScene
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
            error("Not supported")
        }
    }

    class SavableTestConcurrentPairNavigator(
        private val initialScene: Scene<*>,
        savedState: NavigatorState? = null
    ) : ConcurrentPairNavigator(savedState), SavableNavigator {

        override fun createInitialScene(): Scene<out Container> {
            return initialScene
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<*> {
            return when (sceneClass) {
                SavableTestScene::class -> SavableTestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    private open class TestListener : Navigator.Events {

        val scenes = mutableListOf<Pair<Scene<out Container>, TransitionData?>>()
        val lastScene get() = scenes.lastOrNull()?.first
        val lastTransitionData get() = scenes.lastOrNull()?.second

        var finished = false

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            scenes += scene to data
        }

        override fun finished() {
            finished = true
        }
    }

    private open class TestScene(var foo: Int) : Scene<Container>
}
