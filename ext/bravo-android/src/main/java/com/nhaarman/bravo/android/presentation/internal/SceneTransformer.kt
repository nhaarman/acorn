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

package com.nhaarman.bravo.android.presentation.internal

import android.content.Intent
import com.nhaarman.bravo.android.presentation.IntentProvider
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * An interface that can make the distinction between Scenes that should be shown
 * locally in the application itself, and Scenes that should trigger an external
 * app to be launched.
 */
internal interface SceneTransformer {

    /**
     * Transforms given [scene] into a [TransformedScene].
     *
     * @param scene The [Scene] to transform.
     * @param data Any transition data.
     *
     * @return [TransformedScene.ContainerScene] if given scene should be shown
     * locally in the application itself. Return [TransformedScene.ExternalScene]
     * if an external application should be launched.
     */
    fun transform(scene: Scene<out Container>, data: TransitionData?): TransformedScene
}

internal class DefaultSceneTransformer(
    private val intentProvider: IntentProvider
) : SceneTransformer {

    override fun transform(scene: Scene<out Container>, data: TransitionData?): TransformedScene {
        val intent = intentProvider.intentFor(scene, data)
        if (intent == null) {
            return TransformedScene.ContainerScene(scene, data)
        }

        return TransformedScene.ExternalScene(scene, intent)
    }
}

internal sealed class TransformedScene {
    data class ContainerScene(val scene: Scene<out Container>, val data: TransitionData?) : TransformedScene()
    data class ExternalScene(val scene: Scene<out Container>, val intent: Intent) : TransformedScene()
}