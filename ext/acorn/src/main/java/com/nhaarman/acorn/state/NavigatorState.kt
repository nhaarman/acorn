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

package com.nhaarman.bravo.state

import com.nhaarman.bravo.state.internal.BaseSavedState

/**
 * Creates a new [NavigatorState] instance.
 */
fun NavigatorState(): NavigatorState {
    return DefaultNavigatorState(BaseSavedState())
}

/**
 * Creates a new [NavigatorState] instance, providing a DSL-like initialization
 * function.
 */
fun navigatorState(init: (NavigatorState) -> Unit): NavigatorState {
    return NavigatorState().also(init)
}

private data class DefaultNavigatorState(
    private val delegate: SavedState
) : NavigatorState, SavedState by delegate {

    override fun set(key: String, value: SceneState?) {
        setUnchecked(key, value)
    }

    override fun set(key: String, value: NavigatorState?) {
        setUnchecked(key, value)
    }
}
