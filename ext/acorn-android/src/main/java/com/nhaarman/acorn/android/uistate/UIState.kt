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

package com.nhaarman.acorn.android.uistate

import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.nhaarman.acorn.android.internal.v
import com.nhaarman.acorn.android.internal.w
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.android.uistate.internal.Destination
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene

/**
 * A sealed hierarchy that manages layout inflation and [Scene] transition
 * animations.
 *
 * These set of classes form a state machine that can manage switching views
 * when a new Scene becomes active.
 *
 * Invoking methods on this class may cause a state transition: the resulting
 * state is returned. Consumers of this class must call its methods at the
 * appropriate times ([uiVisible], [uiNotVisible], [withScene], [withoutScene])
 * and update their reference to the resulting state accordingly.
 *
 * This class is not thread-safe, and should only be invoked from the main thread.
 */
sealed class UIState {

    /**
     * Denotes that the UI window becomes visible to the user.
     *
     * @return The new state.
     */
    @CheckResult
    abstract fun uiVisible(): UIState

    /**
     * Denotes that the UI window becomes invisible to the user.
     *
     * @return the new state.
     */
    @CheckResult
    abstract fun uiNotVisible(): UIState

    /**
     * Applies given [scene] to the UI.
     *
     * @param scene The new [Scene].
     * @return the new state.
     */
    @CheckResult
    fun withScene(
        scene: Scene<out Container>,
        viewControllerFactory: ViewControllerFactory,
        data: TransitionData?
    ): UIState {
        return withDestination(
            Destination(
                scene,
                viewControllerFactory,
                data
            )
        )
    }

    internal abstract fun withDestination(destination: Destination): UIState

    /**
     * Indicates that there is no local [Scene] currently active.
     *
     * @return the new state.
     */
    @CheckResult
    abstract fun withoutScene(): UIState

    companion object {

        /**
         * Creates the initial [UIState].
         *
         * @param root The [ViewGroup] to show Scene views in, usually
         * [android.R.id.content].
         * @param transitionFactory a [TransitionFactory] that provides
         * [Transition] instances for transition animations.
         */
        fun create(
            root: ViewGroup,
            transitionFactory: TransitionFactory
        ): UIState = NotVisible(root, transitionFactory)
    }
}

/**
 * Represents the initial state where the UI is not visible, and represents no
 * destination.
 */
internal class NotVisible(
    private val root: ViewGroup,
    private val transitionFactory: TransitionFactory
) : UIState() {

    /**
     * Transitions to the [Visible] state, which has no active [Destination].
     */
    override fun uiVisible(): UIState {
        v("UIState.NotVisible", "UI becomes visible.")
        return Visible(root, transitionFactory)
    }

    /**
     * Makes no transition.
     */
    override fun uiNotVisible() = this

    /**
     * Transitions to the [NotVisibleWithDestination] state.
     */
    override fun withDestination(destination: Destination): UIState {
        v("UIState.NotVisible", "Destination changed while not visible: $destination.")
        return NotVisibleWithDestination(
            root,
            transitionFactory,
            destination,
            null
        )
    }

    override fun withoutScene() = this
}

/**
 * Represents the state where the UI is not visible, but does represent a
 * destination. When the UI becomes visible, the scene must be shown directly
 * without any animations.
 */
internal class NotVisibleWithDestination(
    private val root: ViewGroup,
    private val transitionFactory: TransitionFactory,
    private val destination: Destination,
    private val existingViewController: ViewController?
) : UIState() {

    /**
     * Immediately shows the view for the active [Destination] without a
     * transition animation, and transitions to the [VisibleWithDestination] state.
     */
    override fun uiVisible(): UIState {
        if (existingViewController != null) {
            v(
                "UIState.NotVisibleWithDestination",
                "UI becomes visible, attaching container to ${destination.scene}."
            )

            destination.forceAttach(existingViewController)
            return VisibleWithDestination(
                root,
                transitionFactory,
                destination,
                existingViewController
            )
        }

        v(
            "UIState.NotVisibleWithDestination",
            "UI becomes visible with active destination: $destination"
        )
        v("UIState.NotVisibleWithDestination", "Showing destination UI without animation.")

        val viewController = destination.viewControllerFactory.viewControllerFor(destination.scene, root)

        root.removeAllViews()
        root.addView(viewController.view)

        v("UIState.NotVisibleWithDestination", "Attaching container to ${destination.scene}.")
        destination.forceAttach(viewController)

        return VisibleWithDestination(
            root,
            transitionFactory,
            destination,
            viewController
        )
    }

    /**
     * Makes no transition.
     */
    override fun uiNotVisible() = this

    /**
     * Discards the current scene and transitions to a new [NotVisibleWithDestination]
     * state with the new given [destination].
     */
    override fun withDestination(destination: Destination): UIState {
        v("UIState.NotVisibleWithDestination", "Destination changed while not visible to: $destination.")
        return NotVisibleWithDestination(
            root,
            transitionFactory,
            destination,
            null
        )
    }

    override fun withoutScene(): UIState {
        v("UIState.NotVisibleWithDestination", "Destination lost while not visible.")
        return NotVisible(root, transitionFactory)
    }
}

/**
 * Represents the state where the UI is visible without a Scene or View.
 * Entering this state should usually be followed directly with a call to
 * [withDestination], as otherwise the UI would show a blank view.
 */
internal class Visible(
    private val root: ViewGroup,
    private val transitionFactory: TransitionFactory
) : UIState() {

    /**
     * Makes no transition.
     */
    override fun uiVisible() = this

    /**
     * Transitions to the [NotVisible] state.
     */
    override fun uiNotVisible(): UIState {
        v("UIState.Visible", "UI becomes not visible.")
        return NotVisible(root, transitionFactory)
    }

    /**
     * Immediately shows the view for given [Scene] without a transition
     * animation, and transitions to the [VisibleWithDestination] state.
     */
    override fun withDestination(destination: Destination): UIState {
        v("UIState.Visible", "Destination changed with UI visible to: $destination.")
        v(
            "UIState.Visible",
            "No current scene active, showing scene without animation."
        )

        val viewController = destination.viewControllerFactory.viewControllerFor(destination.scene, root)

        root.removeAllViews()
        root.addView(viewController.view)
        destination.forceAttach(viewController)

        return VisibleWithDestination(
            root,
            transitionFactory,
            destination,
            viewController
        )
    }

    override fun withoutScene() = this
}

/**
 * Represents the state where the UI is visible and is actively showing a
 * Scene. This is the state the UI is in most of the time when the application
 * is in the foreground.
 *
 * @param currentDestination The active [Destination].
 * @param currentViewController The [ViewController], must already be attached to
 * the [currentDestination]'s [Scene].
 */
internal class VisibleWithDestination(
    private val root: ViewGroup,
    private val transitionFactory: TransitionFactory,
    private var currentDestination: Destination,
    private var currentViewController: ViewController
) : UIState() {

    private var transitionCallback: CancellableTransitionCallback? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var scheduledDestination: Destination? = null

    /**
     * Makes no transition.
     */
    override fun uiVisible() = this

    /**
     * Detaches the current view from the current [Scene] and transitions to the
     * [NotVisibleWithDestination] state.
     */
    override fun uiNotVisible(): UIState {
        v(
            "VisibleWithDestination",
            "UI becomes invisible, detaching container from ${currentDestination.scene}."
        )
        currentDestination.forceDetach(currentViewController)
        transitionCallback = null

        return NotVisibleWithDestination(
            root,
            transitionFactory,
            currentDestination,
            currentViewController
        )
    }

    /**
     * Executes a transition from the current [Destination] to the given
     * [Destination].
     *
     * If a transition animation is currently active, the transition to
     * [destination] is scheduled.
     */
    override fun withDestination(destination: Destination): UIState {
        v("UIState.VisibleWithDestination", "Scene changed to: $destination.")
        if (transitionCallback != null) {
            v(
                "UIState.VisibleWithDestination",
                "Transition already in progress, scheduling transition to $destination."
            )
            if (scheduledDestination != null) {
                w(
                    "UIState.VisibleWithDestination",
                    "Dropping transition to $scheduledDestination."
                )
            }
            scheduledDestination = destination
            return this
        }

        v("UIState.VisibleWithDestination", "Detaching container from ${currentDestination.scene}.")
        currentDestination.forceDetach(currentViewController)

        v(
            "UIState.VisibleWithDestination",
            "Starting transition from $currentDestination to $destination."
        )
        val callback = MyCallback(destination).also { transitionCallback = it }
        transitionFactory.transitionFor(currentDestination.scene, destination.scene, destination.transitionData)
            .execute(root, callback)

        return this
    }

    override fun withoutScene(): UIState {
        v("UIState.VisibleWithDestination", "Destination lost.")
        if (transitionCallback != null) {
            v("UIState.VisibleWithDestination", "Transition in progress, canceling.")
            transitionCallback = null
            scheduledDestination = null
        } else {
            v("UIState.VisibleWithDestination", "Detaching container from ${currentDestination.scene}.")
            currentDestination.forceDetach(currentViewController)
        }

        return Visible(root, transitionFactory)
    }

    private inner class MyCallback(
        private val newDestination: Destination
    ) : CancellableTransitionCallback {

        private var done = false
            set(done) {
                field = done
                if (done) {
                    transitionCallback = null
                }
            }

        override fun cancel() {
            if (done) return
            done = true

            v(
                "UIState.VisibleWithDestination",
                "Transition to $newDestination cancelled."
            )
        }

        private var attached = false
        override fun attach(viewController: ViewController) {
            if (done) return
            if (attached) return

            v(
                "UIState.VisibleWithDestination",
                "Attaching container to ${newDestination.scene} before transition end."
            )
            attached = true

            currentDestination = newDestination
            currentViewController = viewController
            newDestination.forceAttach(viewController)
        }

        override fun onComplete(viewController: ViewController) {
            if (done) return
            done = true

            v("UIState.VisibleWithDestination", "Transition to $newDestination complete.")

            if (!attached) {
                v(
                    "UIState.VisibleWithDestination",
                    "Container not attached to ${newDestination.scene}; attaching."
                )
                currentDestination = newDestination
                currentViewController = viewController
                currentDestination.forceAttach(viewController)
            }

            scheduledDestination?.let { destination ->
                v("UIState.VisibleWithDestination", "Found scheduled destination: $destination")
                scheduledDestination = null
                withDestination(destination)
            }
        }
    }

    private interface CancellableTransitionCallback : Transition.Callback {

        fun cancel()
    }
}

@Suppress("UNCHECKED_CAST")
private fun Destination.forceAttach(c: Container) {
    (scene as Scene<Container>).attach(c)
}

@Suppress("UNCHECKED_CAST")
private fun Destination.forceDetach(c: Container) {
    (scene as Scene<Container>).detach(c)
}
