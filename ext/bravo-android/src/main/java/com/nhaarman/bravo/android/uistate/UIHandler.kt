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

import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

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
     */
    fun withScene(scene: Scene<out Container>, data: TransitionData?)

    /**
     * Indicates that there is no local [Scene] currently active.
     */
    fun withoutScene()
}
