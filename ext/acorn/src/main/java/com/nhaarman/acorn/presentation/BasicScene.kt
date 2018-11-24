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

/**
 * An abstract [Scene] implementation that saves and restores view state between
 * different views, and provides a reference to the currently attached view.
 *
 * @param V The view type for this [Scene], must implement [RestorableContainer].
 * @property containerState The initial view state for this [Scene].
 * @constructor Creates a new [BasicScene], restoring view state when available.
 */
abstract class BasicScene<V : RestorableContainer> : Scene<V> {

    private var containerState: ContainerState? = null

    /**
     * The currently attached [V] instance, if available.
     * Returns `null` if no instance is attached.
     */
    protected var currentView: V? = null
        private set

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { v.restoreInstanceState(it) }
        currentView = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = v.saveInstanceState()
        currentView = null
    }
}