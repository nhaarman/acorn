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

package com.nhaarman.acorn.presentation

/**
 * A Scene is a destination in the application the user can navigate to.
 *
 * Scenes are the boundary between your application logic and the UI that is
 * displayed to the user.
 *
 * The lifecycle of a Scene is very simple:
 *
 *  - 'stopped'  : The Scene is dormant, waiting to be started or to
 *                 be destroyed.
 *  - 'started'  : The Scene is started.
 *  - 'destroyed': The Scene is destroyed and will not be started anymore.
 *
 * On top of that, the user interface can attach to and detach itself from this
 * Scene via the [attach] and [detach] methods, providing interaction with
 * the user. It is therefore possible that the Scene is started without having
 * a user interface attached.
 *
 * Scenes may implement [SavableScene] to indicate that their instance state
 * can be saved. When this is the case, [SavableScene.saveInstanceState] will
 * be called at the appropriate time.
 *
 * @param V The type of the view to display the Scene, representing the
 *          user interface.
 */
interface Scene<V : Container> {

    /**
     * A unique identifier for this Scene.
     *
     * This key can be used to determine what layout to show, and can be used
     * to save and restore instance state, if needed.
     */
    val key: SceneKey get() = SceneKey.from(javaClass)

    /**
     * Called when this Scene is started.
     */
    fun onStart() {}

    /**
     * Attaches given [V] to this Scene.
     *
     * @param v The user interface that is being attached.
     */
    fun attach(v: V) {}

    /**
     * Detaches any views from this scene.
     *
     * Will always be preceded by a call to [attach].
     */
    fun detach(v: V) {}

    /**
     * Called when this Scene is stopped.
     */
    fun onStop() {}

    /**
     * Called when this Scene will be destroyed.
     *
     * After a call to this method no more calls should be made to this Scene.
     */
    fun onDestroy() {}
}
