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

package com.nhaarman.acorn.android.dispatching

import android.content.Context
import com.nhaarman.acorn.android.dispatching.internal.ActivityHandler
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.presentation.ActivityControllerFactory
import com.nhaarman.acorn.android.uistate.UIHandler
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.android.util.TestViewControllerFactory
import com.nhaarman.acorn.navigation.TestNavigator
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.expect.expectErrorWithMessage
import org.junit.jupiter.api.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class AcornSceneDispatcherTest {

    val navigator = TestNavigator()
    val scene = TestScene.withKey("scene")

    private val viewControllerFactory = TestViewControllerFactory()
    private val activityControllerFactory = TestActivityControllerFactory()

    val uiHandler = mock<UIHandler>()
    val activityHandler = mock<ActivityHandler>()

    val dispatcher = AcornSceneDispatcher(
        context = mock(),
        viewControllerFactory = viewControllerFactory,
        activityControllerFactory = activityControllerFactory,
        uiHandler = uiHandler,
        activityHandler = activityHandler,
        callback = mock(),
    )

    @Test
    fun `an error is thrown for unknown scenes`() {
        // Given
        dispatcher.dispatchScenesFor(navigator)

        // Expect
        expectErrorWithMessage("Could not dispatch") on {
            // When
            navigator.onScene(scene)
        }
    }

    @Test
    fun `dispatching a scene with a view controller`() {
        // Given
        viewControllerFactory.register(scene.key, mock())
        dispatcher.dispatchScenesFor(navigator)

        // When
        navigator.onScene(scene)

        // Then
        verify(uiHandler).withScene(eq(scene), eq(viewControllerFactory), anyOrNull())
    }

    @Test
    fun `dispatching a scene with an activity controller`() {
        // Given
        val controller = mock<ActivityController>()
        activityControllerFactory.register(scene.key, controller)
        dispatcher.dispatchScenesFor(navigator)

        // When
        navigator.onScene(scene)

        // Then
        verify(activityHandler).withScene(scene, controller)
    }

    private class TestActivityControllerFactory : ActivityControllerFactory {

        var factories = mapOf<SceneKey, ActivityController>()

        fun register(sceneKey: SceneKey, factory: ActivityController) {
            factories += sceneKey to factory
        }

        override fun supports(sceneKey: SceneKey): Boolean {
            return factories.containsKey(sceneKey)
        }

        override fun activityControllerFor(scene: Scene<*>, context: Context): ActivityController {
            return factories[scene.key]!!
        }
    }
}
