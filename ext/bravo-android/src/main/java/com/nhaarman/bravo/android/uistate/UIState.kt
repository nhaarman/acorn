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

package com.nhaarman.bravo.android.uistate

import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.nhaarman.bravo.android.internal.v
import com.nhaarman.bravo.android.internal.w
import com.nhaarman.bravo.android.presentation.ViewController
import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.android.transition.Transition
import com.nhaarman.bravo.android.transition.TransitionFactory
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

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
    abstract fun withScene(scene: Scene<out Container>, data: TransitionData?): UIState

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
         * @param viewFactory A [ViewFactory] that provides views for Scenes.
         * @param transitionFactory a [TransitionFactory] that provides
         * [Transition] instances for transition animations.
         */
        fun create(
            root: ViewGroup,
            viewFactory: ViewFactory,
            transitionFactory: TransitionFactory
        ): UIState =
            NotVisible(
                root,
                viewFactory,
                transitionFactory
            )
    }
}

/**
 * Represents the initial state where the UI is not visible, and has no Scene.
 */
internal class NotVisible(
    private val root: ViewGroup,
    private val viewFactory: ViewFactory,
    private val transitionFactory: TransitionFactory
) : UIState() {

    /**
     * Transitions to the [Visible] state, which has no active [Scene].
     */
    override fun uiVisible(): UIState {
        v("UIState.NotVisible", "UI visible.")
        return Visible(root, viewFactory, transitionFactory)
    }

    /**
     * Makes no transition.
     */
    override fun uiNotVisible() = this

    /**
     * Transitions to the [NotVisibleWithScene] state.
     */
    override fun withScene(scene: Scene<out Container>, data: TransitionData?): UIState {
        v("UIState.NotVisible", "Scene changed while not visible: $scene.")
        return NotVisibleWithScene(
            root,
            viewFactory,
            transitionFactory,
            scene,
            null
        )
    }

    override fun withoutScene() = this
}

/**
 * Represents the state where the UI is not visible, but does have a Scene.
 * When the UI becomes visible, the scene must be shown directly * without any
 * animations.
 */
internal class NotVisibleWithScene(
    private val root: ViewGroup,
    private val viewFactory: ViewFactory,
    private val transitionFactory: TransitionFactory,
    private val scene: Scene<out Container>,
    private val existingViewController: ViewController?
) : UIState() {

    /**
     * Immediately shows the view for the active [Scene] without a transition
     * animation, and transitions to the [VisibleWithScene] state.
     */
    override fun uiVisible(): UIState {
        if (existingViewController != null) {
            v(
                "UIState.NotVisibleWithScene",
                "UI visible, attaching container to $scene."
            )

            scene.forceAttach(existingViewController)
            return VisibleWithScene(
                root,
                viewFactory,
                transitionFactory,
                scene,
                existingViewController
            )
        }

        v(
            "UIState.NotVisibleWithScene",
            "UI becomes visible with active scene: $scene"
        )
        v("UIState.NotVisibleWithScene", "Showing scene without animation.")

        val viewController = viewFactory.viewFor(scene.key, root)
            ?: error("No view could be created for Scene with key ${scene.key}.")

        root.removeAllViews()
        root.addView(viewController.view)

        scene.forceAttach(viewController)
        return VisibleWithScene(
            root,
            viewFactory,
            transitionFactory,
            scene,
            viewController
        )
    }

    /**
     * Makes no transition.
     */
    override fun uiNotVisible() = this

    /**
     * Discards the current scene and transitions to a new [NotVisibleWithScene]
     * state with the new given [Scene].
     */
    override fun withScene(scene: Scene<out Container>, data: TransitionData?): UIState {
        v("UIState.NotVisibleWithScene", "Scene changed while not visible to: $scene.")
        return NotVisibleWithScene(
            root,
            viewFactory,
            transitionFactory,
            scene,
            null
        )
    }

    override fun withoutScene(): UIState {
        v("UIState.NotVisibleWithScene", "Scene lost while not visible.")
        return NotVisible(root, viewFactory, transitionFactory)
    }
}

/**
 * Represents the state where the UI is visible without a Scene or View.
 * Entering this state should usually be followed directly with a call to
 * [withScene], as otherwise the UI would show a blank view.
 */
internal class Visible(
    private val root: ViewGroup,
    private val viewFactory: ViewFactory,
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
        v("UIState.Visible", "UI not visible.")
        return NotVisible(root, viewFactory, transitionFactory)
    }

    /**
     * Immediately shows the view for given [Scene] without a transition
     * animation, and transitions to the [VisibleWithScene] state.
     */
    override fun withScene(scene: Scene<out Container>, data: TransitionData?): UIState {
        v("UIState.Visible", "Scene changed while the UI was visible to: $scene.")
        v(
            "UIState.Visible",
            "No current scene active, showing scene without animation."
        )

        val viewController = viewFactory.viewFor(scene.key, root)
            ?: error("No view could be created for Scene with key ${scene.key}.")

        root.removeAllViews()
        root.addView(viewController.view)
        scene.forceAttach(viewController)

        return VisibleWithScene(
            root,
            viewFactory,
            transitionFactory,
            scene,
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
 * @param currentScene The active [Scene].
 * @param currentViewController The [ViewController], must already be attached to
 * [currentScene].
 */
internal class VisibleWithScene(
    private val root: ViewGroup,
    private val viewFactory: ViewFactory,
    private val transitionFactory: TransitionFactory,
    private var currentScene: Scene<out Container>,
    private var currentViewController: ViewController
) : UIState() {

    private var transitionCallback: CancellableTransitionCallback? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var scheduledScene: Pair<Scene<out Container>, TransitionData?>? = null

    /**
     * Makes no transition.
     */
    override fun uiVisible() = this

    /**
     * Detaches the current view from the current [Scene] and transitions to the
     * [NotVisibleWithScene] state.
     */
    override fun uiNotVisible(): UIState {
        v(
            "VisibleWithScene",
            "UI becomes invisible, detaching container from $currentScene."
        )
        currentScene.forceDetach(currentViewController)
        transitionCallback = null

        return NotVisibleWithScene(
            root,
            viewFactory,
            transitionFactory,
            currentScene,
            currentViewController
        )
    }

    /**
     * Executes a transition from the current [Scene] to the given [Scene].
     *
     * If a transition animation is currently active, the transition to [scene]
     * is scheduled.
     */
    override fun withScene(scene: Scene<out Container>, data: TransitionData?): UIState {
        v("UIState.VisibleWithScene", "Scene changed to: $scene.")
        if (transitionCallback != null) {
            v(
                "UIState.VisibleWithScene",
                "Transition already in progress, scheduling transition to $scene."
            )
            if (scheduledScene != null) {
                w(
                    "UIState.VisibleWithScene",
                    "Dropping transition to ${scheduledScene?.first}."
                )
            }
            scheduledScene = scene to data
            return this
        }

        v(
            "UIState.VisibleWithScene",
            "Starting transition from $currentScene to $scene."
        )
        currentScene.forceDetach(currentViewController)

        val callback = MyCallback(scene).also { transitionCallback = it }
        transitionFactory.transitionFor(currentScene, scene, data)
            .execute(root, callback)

        return this
    }

    override fun withoutScene(): UIState {
        v("UIState.VisibleWithScene", "Scene lost.")
        if (transitionCallback != null) {
            v("UIState.VisibleWithScene", "Transition in progress, canceling.")
            transitionCallback = null
            scheduledScene = null
        }

        return Visible(root, viewFactory, transitionFactory)
    }

    private inner class MyCallback(
        private val newScene: Scene<out Container>
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
                "UIState.VisibleWithScene",
                "Transition to $newScene cancelled."
            )
        }

        private var attached = false
        override fun attach(viewController: ViewController) {
            if (done) return
            if (attached) return

            v(
                "UIState.VisibleWithScene",
                "Attaching container to $newScene before transition end."
            )
            attached = true

            currentScene = newScene
            currentViewController = viewController
            newScene.forceAttach(viewController)
        }

        override fun onComplete(viewController: ViewController) {
            if (done) return
            done = true

            v("UIState.VisibleWithScene", "Transition to $newScene complete.")

            if (!attached) {
                v(
                    "UIState.VisibleWithScene",
                    "Container not attached to $newScene; attaching."
                )
                currentScene = newScene
                currentViewController = viewController
                currentScene.forceAttach(viewController)
            }

            scheduledScene?.let { (scene, data) ->
                v("UIState.VisibleWithScene", "Found scheduled scene: $scene")
                scheduledScene = null
                withScene(scene, data)
            }
        }
    }

    private interface CancellableTransitionCallback : Transition.Callback {

        fun cancel()
    }
}

@Suppress("UNCHECKED_CAST")
private fun Scene<out Container>.forceAttach(c: Container) {
    (this as Scene<Container>).attach(c)
}

@Suppress("UNCHECKED_CAST")
private fun Scene<out Container>.forceDetach(c: Container) {
    (this as Scene<Container>).detach(c)
}
