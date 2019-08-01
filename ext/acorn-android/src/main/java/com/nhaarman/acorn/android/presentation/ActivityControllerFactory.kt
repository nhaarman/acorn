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

package com.nhaarman.acorn.android.presentation

import android.content.Context
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

/**
 * A factory interface that can create [ActivityController] instances for [Scene]s.
 */
interface ActivityControllerFactory {

    /**
     * Returns `true` when this ActivityControllerFactory can create an
     * [ActivityController] when [activityControllerFor] is called.
     * If this method returns false for a specific [SceneKey], no calls to
     * [activityControllerFor] with the same SceneKey must be made.
     */
    fun supports(sceneKey: SceneKey): Boolean

    /**
     * Creates an [ActivityController] for given Scene key.
     *
     * @param scene The [Scene] instance for which the corresponding
     * ActivityController should be created.
     * @param context A [Context] instance.
     */
    fun activityControllerFor(scene: Scene<*>, context: Context): ActivityController
}

/**
 * A No-op [ActivityControllerFactory] that does not create any instances.
 */
object NoopActivityControllerFactory : ActivityControllerFactory {

    override fun supports(sceneKey: SceneKey): Boolean {
        return false
    }

    override fun activityControllerFor(scene: Scene<*>, context: Context): ActivityController {
        error("NoopActivityControllerFactory can not create ActivityControllers.")
    }
}
