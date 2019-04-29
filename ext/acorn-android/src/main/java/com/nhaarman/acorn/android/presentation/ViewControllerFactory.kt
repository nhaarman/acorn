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

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

/**
 * A factory interface that can create [ViewController] instances for [Scene]s.
 *
 * This interface uses the [ViewController] interface to be able to provide both
 * the attachable [Container] and the [View] to be displayed to the user.
 */
interface ViewControllerFactory {

    /**
     * Returns `true` when this ViewControllerFactory can create a [ViewController]
     * when [viewControllerFor] is called.
     * If this method returns false for a specific [SceneKey], no calls to
     * [viewControllerFor] with the same SceneKey must be made.
     */
    fun supports(scene: Scene<*>): Boolean

    /**
     * Creates a [ViewController] for given Scene key.
     *
     * @param scene The key of the [Scene] instance for which the
     * corresponding view should be created.
     * @param parent This is the parent [View] that the resulting View should
     * be attached to. The implementation must not add the View to the parent
     * itself, but it can use the parent to generate the LayoutParams of the
     * view.
     *
     * @return The resulting [ViewController].
     */
    fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController
}

/**
 * A [ViewControllerFactory] that uses the [Scene] itself to create [ViewController]
 * instances.
 *
 * This class only supports Scenes that implement the [ViewControllerFactory]
 * interface.
 */
object SceneViewControllerFactory : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return scene is ViewControllerFactory
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        return (scene as ViewControllerFactory).viewControllerFor(scene, parent)
    }
}

/**
 * A [ViewControllerFactory] that cannot create [ViewController] instances.
 */
object NoopViewControllerFactory : ViewControllerFactory {

    override fun supports(scene: Scene<*>): Boolean {
        return false
    }

    override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
        error("NoopViewControllerFactory can not create ViewControllers.")
    }
}
