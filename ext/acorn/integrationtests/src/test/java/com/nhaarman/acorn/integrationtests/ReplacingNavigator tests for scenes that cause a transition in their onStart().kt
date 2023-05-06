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

package com.nhaarman.acorn.integrationtests

import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.ReplacingNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.testing.ContainerProvider
import com.nhaarman.acorn.testing.TestContext
import com.nhaarman.acorn.testing.TestContext.Companion.testWith
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import kotlin.reflect.KClass

/**
 * When a Scene (A) _immediately_ causes another transition to
 * another Scene (B) in its `onStart` method, a wrong order of
 * scene notifications can occur if the listener invocation happens
 * after starting the scene: First B is reported and only then A.
 *
 * These tests form an integration test to ensure proper behavior.
 */
class `ReplacingNavigator tests for scenes that cause a transition in their onStart()` {

    private val navigator = MyNavigator()
    private val navigatorListener = mock<Navigator.Events>()

    private val immediatelyFinishingScene = ImmediatelyFinishingScene { navigator.finish() }

    private val context = TestContext.create(
        navigator,
        object : ContainerProvider {
            override fun containerFor(scene: Scene<*>): Container {
                return mock()
            }
        },
    )

    @Test
    fun `replacing an immediately finishing scene results in proper navigator notifications`() = testWith(context) {
        // Given
        navigator.addNavigatorEventsListener(navigatorListener)

        // When
        navigator.replace(immediatelyFinishingScene)

        // Then
        inOrder(navigatorListener) {
            verify(navigatorListener).scene(any<InitialScene>(), anyOrNull())
            verify(navigatorListener).scene(any<ImmediatelyFinishingScene>(), anyOrNull())
            verify(navigatorListener).finished()
            verifyNoMoreInteractions()
        }
    }

    private class InitialScene : Scene<Container>

    private class ImmediatelyFinishingScene(private val onFinish: () -> Unit) : Scene<Container> {

        override fun onStart() {
            onFinish()
        }
    }

    private class MyNavigator : ReplacingNavigator(null) {

        override fun initialScene(): Scene<out Container> {
            return InitialScene()
        }

        override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
            error("Not used")
        }
    }
}
