/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
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
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

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
        callback = mock()
    )

    @Test
    fun `an error is thrown for unknown scenes`() {
        /* Given */
        dispatcher.dispatchScenesFor(navigator)

        /* Expect */
        expectErrorWithMessage("Could not dispatch") on {

            /* When */
            navigator.onScene(scene)
        }
    }

    @Test
    fun `dispatching a scene with a view controller`() {
        /* Given */
        viewControllerFactory.register(scene.key, mock())
        dispatcher.dispatchScenesFor(navigator)

        /* When */
        navigator.onScene(scene)

        /* Then */
        verify(uiHandler).withScene(eq(scene), eq(viewControllerFactory), anyOrNull())
    }

    @Test
    fun `dispatching a scene with an activity controller`() {
        /* Given */
        val controller = mock<ActivityController>()
        activityControllerFactory.register(scene.key, controller)
        dispatcher.dispatchScenesFor(navigator)

        /* When */
        navigator.onScene(scene)

        /* Then */
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