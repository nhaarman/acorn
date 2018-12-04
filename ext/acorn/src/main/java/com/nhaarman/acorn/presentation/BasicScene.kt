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

/**
 * A basic abstract [Scene] implementation that provides some basic functionality.
 *
 * This class provides an [attachedView] property which provides the currently
 * attached [V] instance, if available.
 *
 * @param V The view type for this [Scene]. Can implement [RestorableContainer]
 * to save and restore view state between different views attached to the Scene.
 * @property containerState The initial view state for this [Scene].
 * May be `null`.
 * @constructor Creates a new [BasicScene], restoring view state when available.
 */
abstract class BasicScene<V : Container>(
    private var containerState: ContainerState? = null
) : Scene<V> {

    /**
     * The currently attached [V] instance, if available.
     *
     * This property will be updated when [attach] or [detach] is called.
     *
     * Returns `null` if no instance is attached.
     */
    protected var attachedView: V? = null
        private set

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { (v as? RestorableContainer)?.restoreInstanceState(it) }
        attachedView = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = (v as? RestorableContainer)?.saveInstanceState()
        attachedView = null
    }
}