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

import com.nhaarman.bravo.android.presentation.ViewController
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

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
     * Applies given [scene] to the UI.
     *
     * Depending on the current internal state the Scene change may occur
     * directly or be scheduled, for example when a transition animation is
     * running.
     *
     * @param scene The new [Scene] to apply.
     * @param viewControllerProvider The [ViewControllerProvider] that can provide
     * the [ViewController] for given [scene].
     * @param data Any [TransitionData] to be used for transitions.
     */
    fun withScene(
        scene: Scene<out Container>,
        viewControllerProvider: ViewControllerProvider,
        data: TransitionData?
    )

    /**
     * Indicates that there is no local [Scene] currently active.
     */
    fun withoutScene()
}
