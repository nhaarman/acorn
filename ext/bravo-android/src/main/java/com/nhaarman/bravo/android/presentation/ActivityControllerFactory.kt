/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.presentation

import android.content.Context
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey

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
     * @param sceneKey The key of the [Scene] instance for which the corresponding
     * ActivityController should be created.
     * @param context A [Context] instance.
     */
    fun activityControllerFor(sceneKey: SceneKey, context: Context): ActivityController
}

/**
 * A No-op [ActivityControllerFactory] that does not create any instances.
 */
object NoopActivityControllerFactory : ActivityControllerFactory {

    override fun supports(sceneKey: SceneKey): Boolean {
        return false
    }

    override fun activityControllerFor(sceneKey: SceneKey, context: Context): ActivityController {
        error("NoopActivityControllerFactory can not create ActivityControllers.")
    }
}