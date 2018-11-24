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

package com.nhaarman.bravo.presentation

/**
 * A Scene is a destination in the application the user can navigate to.
 *
 * Scenes are the boundary between your application logic and the UI that is
 * displayed to the user.
 *
 * The lifecycle of a Scene is very simple:
 *
 *  - 'inactive' : The Scene is dormant, waiting to become active or to
 *                 be destroyed.
 *  - 'active'   : The Scene is active.
 *  - 'destroyed': The Scene is destroyed and will not become active anymore.
 *
 * On top of that, the user interface can attach to and detach itself from this
 * Scene via the [attach] and [detach] methods, providing interaction with
 * the user. It is therefore possible that the Scene is active without having
 * a user interface attached.
 *
 * Scenes may implement [SaveableScene] to indicate that their instance state
 * can be saved. When this is the case, [SaveableScene.saveInstanceState] will
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
     * Called when this Scene becomes active.
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
     * Called when this Scene becomes inactive.
     */
    fun onStop() {}

    /**
     * Called when this Scene will be destroyed.
     *
     * After a call to this method no more calls should be made to this Scene.
     */
    fun onDestroy() {}
}