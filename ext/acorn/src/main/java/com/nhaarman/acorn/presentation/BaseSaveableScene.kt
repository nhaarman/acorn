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

import androidx.annotation.CallSuper
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.get
import com.nhaarman.bravo.state.sceneState

/**
 * A saveable [Scene] implementation that saves and restores view state between
 * different views, and implements [SaveableScene] to be able to save the
 * Scene itself.
 *
 * Calling [saveInstanceState] on this class will also save the view state,
 * if available.
 *
 * @param V The view type for this [Scene], must implement [RestorableContainer].
 * @param savedState A previous saved state instance for this [Scene].
 * May be `null`.
 * @constructor Creates a new [SaveableScene], restoring view state when available.
 */
abstract class BaseSaveableScene<V : RestorableContainer>(
    savedState: SceneState?
) : Scene<V>, SaveableScene {

    private var view: V? = null
    private var containerState: ContainerState? = savedState?.containerState

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { v.restoreInstanceState(it) }
        containerState = null
        view = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = v.saveInstanceState()
        view = null
    }

    @CallSuper
    override fun saveInstanceState(): SceneState {
        return sceneState {
            it.containerState = containerState ?: view?.saveInstanceState()
        }
    }

    companion object {

        private var SceneState.containerState: ContainerState?
            set(value) {
                this["view_state"] = value
            }
            get() = get("view_state")
    }
}