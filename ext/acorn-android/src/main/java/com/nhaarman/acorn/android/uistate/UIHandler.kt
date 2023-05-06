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

import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene

/**
 * An interface that provides UI handling when working with Scenes.
 *
 * @see UIStateUIHandler
 */
interface UIHandler {

    /**
     * Denotes that the UI window becomes visible to the user,
     * for example when the Activity enters its 'started' state.
     */
    fun onUIVisible()

    /**
     * Denotes that the UI window becomes invisible to the user,
     * for example when the Activity enters its 'stopped' state.
     */
    fun onUINotVisible()

    /**
     * Invoked when the user presses the back button.
     *
     * Implementations can consume the event by returning `true` and stop
     * further propagation of the event.
     *
     * @return true if the event is consumed.
     */
    fun onBackPressed(): Boolean

    /**
     * Applies given [scene] to the UI.
     *
     * Depending on the current internal state the Scene change may occur
     * directly or be scheduled, for example when a transition animation is
     * running.
     *
     * @param scene The new [Scene] to apply.
     * @param viewControllerFactory The [ViewControllerFactory] that can provide
     * the [ViewController] for given [scene].
     * @param data Any [TransitionData] to be used for transitions.
     */
    fun withScene(
        scene: Scene<out Container>,
        viewControllerFactory: ViewControllerFactory,
        data: TransitionData?,
    )

    /**
     * Indicates that there is no local [Scene] currently active.
     */
    fun withoutScene()
}
