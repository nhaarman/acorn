package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.StateSaveable

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
 * Scenes may implement [StateSaveable] to indicate that their instance state
 * can be saved. When this is the case, [StateSaveable.saveInstanceState] will
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
    val key: String get() = this.javaClass.name

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