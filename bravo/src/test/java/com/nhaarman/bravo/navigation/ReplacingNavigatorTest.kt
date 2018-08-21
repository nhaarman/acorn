package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.BravoBundle
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

internal class ReplacingNavigatorTest {

    private val initialScene get() = navigator.initialScenes[0]

    private val scene1 = spy(TestScene(1))

    private val navigator = TestReplacingNavigator(null)

    private val listener = TestListener()

    @Nested
    inner class NavigatorState {

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
            expect(listener.lastScene).toBe(initialScene)
        }

        @Test
        fun `starting navigator notifies listeners of scene`() {
            /* Given */
            navigator.addListener(listener)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(initialScene)
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

        @Test
        fun `replacing scene notifies listener of new scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()

            /* When */
            navigator.replace(scene1)

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `registering listener after scene change notifies new scene`() {
            /* Given */
            navigator.onStart()
            navigator.replace(scene1)

            /* When */
            navigator.addListener(listener)

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `replacing scene after navigator stopped does not notify listener of new scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()
            navigator.onStop()

            /* When */
            navigator.replace(scene1)

            /* Then */
            expect(listener.lastScene).toBe(initialScene)
        }

        @Test
        fun `replacing scene after navigator destroyed does not notify listener of new scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()
            navigator.onDestroy()

            /* When */
            navigator.replace(scene1)

            /* Then */
            expect(listener.lastScene).toBe(initialScene)
        }

        @Test
        fun `starting navigator after scene changed in inactive state notifies listeners of new scene`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()
            navigator.onStop()
            navigator.replace(scene1)

            /* When */
            navigator.onStart()

            /* Then */
            expect(listener.lastScene).toBe(scene1)
        }

        @Test
        fun `onBackPressed after scene change notifies listeners of finished`() {
            /* Given */
            navigator.addListener(listener)
            navigator.onStart()
            navigator.replace(scene1)

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
            initialScene.inOrder {
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
            initialScene.inOrder {
                verify().onStart()
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `stopping an inactive navigator does not stop Scene`() {
            /* When */
            navigator.onStop()

            /* Then */
            verifyNoMoreInteractions(initialScene)
        }

        @Test
        fun `stopping an active navigator stops Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onStop()

            /* Then */
            initialScene.inOrder {
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
            verify(initialScene, never()).onStop()
        }

        @Test
        fun `destroy an inactive navigator does destroy Scene`() {
            /* When */
            navigator.onDestroy()

            /* Then */
            verify(initialScene).onDestroy()
        }

        @Test
        fun `destroy an active navigator stops and destroys Scene`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.onDestroy()

            /* Then */
            initialScene.inOrder {
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
            verify(initialScene).onDestroy()
            verify(initialScene, never()).onStart()
        }

        @Test
        fun `stopping a destroyed navigator does not start Scene`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onStop()

            /* Then */
            verify(initialScene).onDestroy()
            verify(initialScene, never()).onStop()
        }

        @Test
        fun `destroying a destroyed navigator only destroys Scene once`() {
            /* Given */
            navigator.onDestroy()

            /* When */
            navigator.onDestroy()

            /* Then */
            verify(initialScene, times(1)).onDestroy()
        }

        @Test
        fun `changing scene stops and destroys previous screen`() {
            /* Given */
            navigator.onStart()

            /* When */
            navigator.replace(scene1)

            /* Then */
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
            /* Given */
            navigator.onStart()
            navigator.replace(scene1)

            /* When */
            navigator.onStop()

            /* Then */
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
            /* When */
            navigator.onStart()
            navigator.onStop()
            navigator.onStart()
            navigator.onStop()
            navigator.onStart()
            navigator.onDestroy()

            /* Then */
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

        private val navigator = RestorableReplacingNavigator(null)
        private val scene1 = TestScene(1)

        @Test
        fun `saving and restoring state for initial scene`() {
            /* Given */
            navigator.onStart()
            navigator.initialScene.foo = 3

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = RestorableReplacingNavigator(bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(3)
        }

        @Test
        fun `saving and restoring state for replaced scene`() {
            /* Given */
            navigator.onStart()
            navigator.replace(scene1)
            scene1.foo = 42

            /* When */
            val bundle = navigator.saveInstanceState()
            val restoredNavigator = RestorableReplacingNavigator(bundle)
            restoredNavigator.onStart()
            restoredNavigator.addListener(listener)

            /* Then */
            expect(listener.lastScene?.foo).toBe(42)
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

    private class TestReplacingNavigator(savedState: BravoBundle?) : ReplacingNavigator(savedState) {

        val initialScenes = listOf(
            spy(TestScene(0)),
            spy(TestScene(0))
        )

        private var creationCount = 0

        val initialSceneCreator = {
            initialScenes[creationCount]
                .also { creationCount++ }
        }

        override fun initialScene(): Scene<*> {
            return initialSceneCreator.invoke()
        }

        override fun instantiateScene(sceneClass: Class<*>, state: BravoBundle?): Scene<*> {
            return when (sceneClass) {
                TestScene::class.java -> TestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }

    private class RestorableReplacingNavigator(savedState: BravoBundle?) : ReplacingNavigator(savedState) {

        val initialScene = TestScene(0)
        override fun initialScene(): Scene<*> {
            return initialScene
        }

        override fun instantiateScene(sceneClass: Class<*>, state: BravoBundle?): Scene<*> {
            return when (sceneClass) {
                TestScene::class.java -> TestScene.create(state)
                else -> error("Unknown class: $sceneClass")
            }
        }
    }
}
