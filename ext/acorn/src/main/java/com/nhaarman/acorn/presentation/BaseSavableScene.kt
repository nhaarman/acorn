/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.presentation

import androidx.annotation.CallSuper
import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.sceneState

/**
 * An abstract base [Scene] implementation that provides commonly used
 * functionality.
 *
 * This class provides an [attachedView] property which provides the currently
 * attached [V] instance, if available.
 *
 * If the [Container] type [V] implements [RestorableContainer], this class will
 * save and restore the view state between subsequent calls to [attach] and
 * [detach].
 *
 * This class implements [SavableScene] and will by default save the view state
 * in [saveInstanceState], if [V] implements [RestorableContainer].
 *
 * @param V The view type for this [Scene]. Can implement [RestorableContainer]
 * to save and restore view state between different views attached to the Scene.
 * @param savedState A previous saved state instance for this [Scene].
 * May be `null`.
 * @constructor Creates a new [BaseSavableScene], restoring view state when
 * available.
 */
abstract class BaseSavableScene<V : Container>(
    savedState: SceneState?
) : Scene<V>, SavableScene {

    /**
     * The currently attached [V] instance, if available.
     *
     * This property will be updated when [attach] or [detach] is called.
     *
     * Returns `null` if no instance is attached.
     */
    protected var attachedView: V? = null
        private set

    private var containerState: ContainerState? = savedState?.containerState

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { (v as? RestorableContainer)?.restoreInstanceState(it) }
        containerState = null
        attachedView = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = (v as? RestorableContainer)?.saveInstanceState()
        attachedView = null
    }

    /**
     * Saves the instance state for this Scene.
     *
     * The default implementation of this method will save the view state if [V]
     * implements [RestorableContainer].
     *
     * Implementers of this class may override this method to save additional
     * information.
     * However, it is recommended to call the default implementation.
     */
    @CallSuper
    override fun saveInstanceState(): SceneState {
        return sceneState {
            it.containerState = containerState
                ?: (attachedView as? RestorableContainer)?.saveInstanceState()
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