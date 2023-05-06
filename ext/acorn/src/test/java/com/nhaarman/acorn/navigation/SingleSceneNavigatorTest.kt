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
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class SingleSceneNavigatorTest {

    val navigator = TestSingleSceneNavigator()

    private val listener = spy(TestListener())

    @Nested
    inner class NavigatorStates {

        @Test
        fun `inactive navigator is not finished`() {
            // When
            navigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `inactive navigator is not destroyed`() {
            // Then
            expect(navigator.isDestroyed()).toBe(false)
        }

        @Test
        fun `active navigator is not destroyed`() {
            // Given
            navigator.onStart()

            // Then
            expect(navigator.isDestroyed()).toBe(false)
        }

        @Test
        fun `stopped navigator is not destroyed`() {
            // Given
            navigator.onStart()

            // When
            navigator.onStop()

            // Then
            expect(navigator.isDestroyed()).toBe(false)
        }

        @Test
        fun `destroyed navigator is destroyed`() {
            // When
            navigator.onDestroy()

            // Then
            expect(navigator.isDestroyed()).toBe(true)
        }

        @Test
        fun `inactive navigator does not notify newly added listener of scene`() {
            // When
            navigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastScene).toBeNull()
        }

        @Test
        fun `active navigator does notify newly added listener of scene`() {
            // Given
            navigator.onStart()

            // When
            navigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastScene).toBe(navigator.scene)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.onStart()

            // Then
            expect(listener.lastScene).toBe(navigator.scene)
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
        fun `starting navigator does not finish`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.onStart()

            // Then
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `stopping navigator does not finish`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.onStop()

            // Then
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `destroying navigator does not finish`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.onDestroy()

            // Then
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `onBackPressed notifies listeners of finished`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            val result = navigator.onBackPressed()

            // Then
            expect(result).toBe(true)
            expect(listener.finished).toBe(true)
        }

        @Test
        fun `finish notifies listeners of finished`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.finish()

            // Then
            expect(listener.finished).toBe(true)
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
            expect(listener.finished).toBe(false)
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
            expect(listener.finished).toBe(false)
        }

        @Test
        fun `starting navigator twice only notifies listener once`() {
            // Given
            val listener = mock<Navigator.Events>()
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
            val listener = mock<Navigator.Events>()
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
    }

    @Nested
    inner class SceneInteraction {

        @Test
        fun `starting navigator starts Scene`() {
            // When
            navigator.onStart()

            // Then
            navigator.scene.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting navigator multiple times starts Scene only once`() {
            // When
            navigator.onStart()
            navigator.onStart()

            // Then
            navigator.scene.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop Scene`() {
            // When
            navigator.onStop()

            // Then
            verifyNoMoreInteractions(navigator.scene)
        }

        @Test
        fun `stopping an active navigator stops Scene`() {
            // Given
            navigator.onStart()

            // When
            navigator.onStop()

            // Then
            navigator.scene.inOrder {
                verify().onStart()
                verify().onStop()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `destroy an inactive navigator does not stop Scene`() {
            // When
            navigator.onDestroy()

            // Then
            verify(navigator.scene, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy Scene`() {
            // When
            navigator.onDestroy()

            // Then
            verify(navigator.scene).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys Scene`() {
            // Given
            navigator.onStart()

            // When
            navigator.onDestroy()

            // Then
            navigator.scene.inOrder {
                verify().onStart()
                verify().onStop()
                verify().onDestroy()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `starting a destroyed navigator does not start Scene`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStart()

            // Then
            verify(navigator.scene).onDestroy()
            verify(navigator.scene, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start Scene`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStop()

            // Then
            verify(navigator.scene).onDestroy()
            verify(navigator.scene, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys Scene once`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onDestroy()

            // Then
            verify(navigator.scene, times(1)).onDestroy()
        }

        @Test
        fun `starting and stopping navigator multiple times and finally destroying`() {
            // When
            navigator.onStart()
            navigator.onStop()
            navigator.onStart()
            navigator.onStop()
            navigator.onStart()
            navigator.onDestroy()

            // Then
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
        fun `SingleSceneNavigator does not implement SavableNavigator by default`() {
            // Given
            val navigator: Navigator = TestSingleSceneNavigator()

            // Then
            expect(navigator is SavableNavigator).toBe(false)
        }

        @Test
        fun `saving and restoring state for savable navigator and scene`() {
            // Given
            val scene1 = SavableTestScene(foo = 3)
            val navigator = SavableSingleSceneNavigator(null) { scene1 }
            navigator.onStart()

            // When
            val bundle = navigator.saveInstanceState()
            scene1.foo = 4
            val restoredNavigator = SavableSingleSceneNavigator(bundle) { SavableTestScene.create(it) }
            restoredNavigator.onStart()

            // Then
            expect(restoredNavigator.scene).toBeInstanceOf<SavableTestScene> {
                expect(it.foo).toBe(3)
            }
        }

        @Test
        fun `saving and restoring state for savable navigator and non savable scene`() {
            // Given
            val scene1 = TestScene(foo = 3)
            val navigator = SavableSingleSceneNavigator(null) { scene1 }
            navigator.onStart()

            // When
            val bundle = navigator.saveInstanceState()
            scene1.foo = 4
            val restoredNavigator = SavableSingleSceneNavigator(bundle) {
                TestScene(foo = 5)
            }
            restoredNavigator.onStart()

            // Then
            expect(restoredNavigator.scene).toBeInstanceOf<TestScene> {
                expect(it.foo).toBe(5)
            }
        }
    }

    private open class TestListener : Navigator.Events {

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

    class TestSingleSceneNavigator : SingleSceneNavigator(null) {

        lateinit var scene: SavableTestScene

        override fun createScene(state: SceneState?): Scene<out Container> {
            return spy(SavableTestScene.create(state)).also { scene = it }
        }
    }

    class SavableSingleSceneNavigator(
        savedState: NavigatorState?,
        private val sceneCreator: (SceneState?) -> Scene<out Container>,
    ) : SingleSceneNavigator(savedState),
        SavableNavigator {

        lateinit var scene: Scene<out Container>

        override fun createScene(state: SceneState?): Scene<out Container> {
            return spy(sceneCreator.invoke(state)).also { scene = it }
        }
    }
}
