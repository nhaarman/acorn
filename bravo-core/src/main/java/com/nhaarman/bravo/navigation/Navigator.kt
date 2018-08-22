package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable

/**
 * An interface that describes a flow through the application.
 *
 * The Navigator is a class that takes care of navigating the user through an
 * application by showing a sequence of Scenes.
 * Interested parties may subscribe a listener using [addListener], through which
 * Scene changes will be published.
 *
 * Navigators are responsible for handling the lifecycles of the Scenes they
 * manage. To be able to do this, the Navigator has a very simple lifecycle as
 * well:
 *
 *  - `inactive` : The Navigator is currently idle and will not emit any changes
 *                 in Scenery.
 *  - `active`   : The Navigator is currently active and will notify interested
 *                 parties of changes in Scenes.
 *  - `destroyed`: The Navigator has been destroyed and will not become active
 *                 anymore.
 *
 * Navigator instance start in the `inactive` state and can switch between
 * `inactive` and `active` an infinite amount of times.
 * Once a Navigator has been destroyed, it must be considered as dead, and any
 * further interactions with it will be ignored.
 *
 * Navigators that are not `active` must never have Scenes in their `active`
 * state.
 *
 * Navigators may implement [SaveableNavigator] to indicate that their instance state
 * can be saved. When this is the case, [SaveableNavigator.saveInstanceState] will
 * be called at the appropriate time.
 */
interface Navigator<E : Navigator.Events> {

    /**
     * Registers given [listener] with this Navigator.
     *
     * @return a [Disposable] instance that can be disposed when the [listener]
     * is not interested in events anymore.
     */
    @CheckReturnValue
    fun addListener(listener: E): Disposable

    /**
     * Starts this Navigator.
     *
     * Calling this method when the Navigator is not started or destroyed triggers
     * a call to [Scene.onStart] for the [Scene] that is currently active in the
     * Navigator.
     * Listeners registered with [addListener] will be notified of that Scene
     * through [Events.scene].
     *
     * Calling this method when the Navigator is started or destroyed has no effect.
     */
    fun onStart()

    /**
     * Stops this Navigator.
     *
     * Calling this method when the Navigator is started triggers a call to
     * [Scene.onStop] for any [Scene]s that are currently active in the
     * Navigator.
     *
     * Calling this method when the Navigator is stopped or destroyed has no effect.
     */
    fun onStop()

    /**
     * Destroys this Navigator.
     *
     * Calling this method when the Navigator is started will trigger a call to
     * [Scene.onStop] for the [Scene] that is currently active in the Navigator.
     * Furthermore, a call to [Scene.onDestroy] is triggered for _every_ Scene
     * this Navigator is managing.
     *
     * Calling this method when the Navigator is stopped triggers a call to
     * [Scene.onDestroy] for every Scene this Navigator is managing.
     *
     * Calling this method when the Navigator is destroyed has no effect.
     *
     * When this method has been called, the Navigator must be considered as dead,
     * and no calls to [onStart] or [onStop] should be done anymore.
     */
    fun onDestroy()

    /**
     * An interface that is used to notify interested parties of Scene changes
     * or finish events.
     *
     * Navigator implementations can extend this interface to add functionality.
     */
    interface Events {

        /**
         * Called when a [Scene] change occurs in the Navigator.
         *
         * Will only be called if a Scene change occurs when the Navigator is in
         * the started state, or when the Navigator enters the started state.
         *
         * @param scene the newly active [Scene].
         */
        fun scene(scene: Scene<out Container>)

        /**
         * Called when the Navigator has finished.
         *
         * Finish events occur when the Navigator has no more Scenes to show,
         * such as a stack-based Navigator with an empty stack, or a wizard
         * Navigator that reached the end of the wizard.
         */
        fun finished()
    }
}