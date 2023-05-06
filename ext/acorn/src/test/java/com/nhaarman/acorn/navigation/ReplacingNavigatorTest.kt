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
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.reflect.KClass

internal class ReplacingNavigatorTest {

    private val initialScene get() = navigator.initialScenes[0]

    private val scene1 = spy(SavableTestScene(1))

    private val navigator = TestReplacingNavigator()

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
            expect(listener.lastSavableScene).toBeNull()
        }

        @Test
        fun `active navigator does notify newly added listener of scene`() {
            // Given
            navigator.onStart()

            // When
            navigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastSavableScene).toBe(initialScene)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.onStart()

            // Then
            expect(listener.lastSavableScene).toBe(initialScene)
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
        fun `starting navigator multiple times notifies listeners of scene only once`() {
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
        fun `replacing scene notifies listener of new scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()

            // When
            navigator.replace(scene1)

            // Then
            expect(listener.lastSavableScene).toBe(scene1)
        }

        @Test
        fun `registering listener after scene change notifies new scene`() {
            // Given
            navigator.onStart()
            navigator.replace(scene1)

            // When
            navigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastSavableScene).toBe(scene1)
        }

        @Test
        fun `replacing scene after navigator stopped does not notify listener of new scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.onStop()

            // When
            navigator.replace(scene1)

            // Then
            expect(listener.lastSavableScene).toBe(initialScene)
        }

        @Test
        fun `replacing scene after navigator destroyed does not notify listener of new scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.onDestroy()

            // When
            navigator.replace(scene1)

            // Then
            expect(listener.lastSavableScene).toBe(initialScene)
        }

        @Test
        fun `starting navigator after scene changed in inactive state notifies listeners of new scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.onStop()
            navigator.replace(scene1)

            // When
            navigator.onStart()

            // Then
            expect(listener.lastSavableScene).toBe(scene1)
        }

        @Test
        fun `onBackPressed after scene change notifies listeners of finished`() {
            // Given
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()
            navigator.replace(scene1)

            // When
            val result = navigator.onBackPressed()

            // Then
            expect(result).toBe(true)
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
    }

    @Nested
    inner class SceneBehavior {

        @Test
        fun `starting navigator starts Scene`() {
            // When
            navigator.onStart()

            // Then
            initialScene.inOrder {
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
            initialScene.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop Scene`() {
            // When
            navigator.onStop()

            // Then
            verifyNoMoreInteractions(initialScene)
        }

        @Test
        fun `stopping an active navigator stops Scene`() {
            // Given
            navigator.onStart()

            // When
            navigator.onStop()

            // Then
            initialScene.inOrder {
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
            verify(initialScene, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy Scene`() {
            // When
            navigator.onDestroy()

            // Then
            verify(initialScene).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys Scene`() {
            // Given
            navigator.onStart()

            // When
            navigator.onDestroy()

            // Then
            initialScene.inOrder {
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
            verify(initialScene).onDestroy()
            verify(initialScene, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start Scene`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onStop()

            // Then
            verify(initialScene).onDestroy()
            verify(initialScene, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys Scene once`() {
            // Given
            navigator.onDestroy()

            // When
            navigator.onDestroy()

            // Then
            verify(initialScene, times(1)).onDestroy()
        }

        @Test
        fun `changing scene stops and destroys previous screen`() {
            // Given
            navigator.onStart()

            // When
            navigator.replace(scene1)

            // Then
            inOrder(initialScene, scene1) {
                verify(initialScene).onStart()
                verify(initialScene).onStop()
                verify(initialScene).onDestroy()
                verify(scene1).onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping navigator after changed scene stops proper scene`() {
            // Given
            navigator.onStart()
            navigator.replace(scene1)

            // When
            navigator.onStop()

            // Then
            inOrder(initialScene, scene1) {
                verify(initialScene).onStart()
                verify(initialScene).onStop()
                verify(initialScene).onDestroy()
                verify(scene1).onStart()
                verify(scene1).onStop()
                verifyNoMoreInteractions()
            }
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
            initialScene.inOrder {
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

        private val scene1 = TestScene(1)
        private val scene2 = TestScene(2)
        private val savableScene1 = SavableTestScene(1)
        private val savableScene2 = SavableTestScene(2)

        @Test
        fun `ReplacingNavigator does not implement SavableNavigator by default`() {
            // Given
            val navigator: Navigator = TestReplacingNavigator()

            // Then
            expect(navigator is SavableNavigator).toBe(false)
        }

        @Test
        fun `saving and restoring state for savable initial scene`() {
            // Given
            val navigator = SavableReplacingNavigator(null, savableScene1)
            navigator.onStart()

            // When
            val bundle = navigator.saveInstanceState()
            savableScene1.foo = 3
            val restoredNavigator = SavableReplacingNavigator(bundle, savableScene2)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastSavableScene?.foo).toBe(1)
        }

        @Test
        fun `saving and restoring state for replaced savable scene`() {
            // Given
            val navigator = SavableReplacingNavigator(null, savableScene1)
            navigator.onStart()
            navigator.replace(savableScene2)
            savableScene2.foo = 42

            // When
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableReplacingNavigator(bundle, savableScene1)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastSavableScene?.foo).toBe(42)
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
            navigator.replace(scene1)
            val state2 = navigator.saveInstanceState()

            // Then
            expect(state1).toBe(state2)
        }

        @Test
        fun `saving and restoring state for non savable initial scene`() {
            // Given
            val navigator = SavableReplacingNavigator(null, scene1)
            navigator.onStart()

            // When
            val bundle = navigator.saveInstanceState()
            scene1.foo = 3
            val restoredNavigator = SavableReplacingNavigator(bundle, scene2)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastScene).toBe(scene2)
        }

        @Test
        fun `saving and restoring state for replaced non savable scene`() {
            // Given
            val navigator = SavableReplacingNavigator(null, scene1)
            navigator.onStart()
            navigator.replace(scene2)
            scene2.foo = 42

            // When
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = SavableReplacingNavigator(bundle, scene1)
            restoredNavigator.onStart()
            restoredNavigator.addNavigatorEventsListener(listener)

            // Then
            expect(listener.lastScene).toBe(scene1)
        }
    }

    @Nested
    inner class `Order of scene start and listener invocation` {

        /**
         * A Scene (A) that _immediately_ causes another transition to
         * another Scene (B) when A's `onStart` method is invoked results
         * in the wrong order of scene notifications if the listener
         * invocation happens after starting the scene: First B is reported
         * and only then A.
         *
         * Ensuring listener invocation happens before starting the Scene
         * resolves this issue.
         */

        @Test
        fun `replacing a scene invokes listeners before starting the new scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)
            navigator.onStart()

            // When
            navigator.replace(scene1)

            // Then
            inOrder(listener, scene1) {
                verify(listener).scene(eq(scene1), anyOrNull())
                verify(scene1).onStart()
            }
        }

        @Test
        fun `starting the navigator invokes listeners before starting the scene`() {
            // Given
            navigator.addNavigatorEventsListener(listener)

            // When
            navigator.onStart()

            // Then
            inOrder(listener, initialScene) {
                verify(listener).scene(eq(initialScene), anyOrNull())
                verify(initialScene).onStart()
            }
        }
    }

    private open class TestListener : Navigator.Events {

        val scenes = mutableListOf<Scene<out Container>>()
        val lastScene get() = scenes.lastOrNull() as TestScene?
        val lastSavableScene get() = scenes.lastOrNull() as SavableTestScene?

        var finished = false

        override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            scenes += scene
        }

        override fun finished() {
            finished = true
        }
    }

    private class TestReplacingNavigator : ReplacingNavigator(null) {

        val initialScenes = listOf(
            spy(SavableTestScene(0)),
            spy(SavableTestScene(0)),
        )

        private var creationCount = 0

        val initialSceneCreator = {
            initialScenes[creationCount]
                .also { creationCount++ }
        }

        override fun initialScene(): Scene<*> {
            return initialSceneCreator.invoke()
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<*> {
            return when (sceneClass) {
                SavableTestScene::class -> SavableTestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    private class SavableReplacingNavigator(
        savedState: NavigatorState?,
        val initialScene: Scene<*>,
    ) : ReplacingNavigator(savedState), SavableNavigator {

        override fun initialScene(): Scene<*> {
            return initialScene
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<*> {
            return when (sceneClass) {
                SavableTestScene::class -> SavableTestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }
}
