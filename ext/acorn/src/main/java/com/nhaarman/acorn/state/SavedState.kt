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
 * Creates a new [SavedState] instance.
 */
fun SavedState(): SavedState {
    return BaseSavedState()
}

/**
 * Creates a new [SavedState] instance, providing a DSL-like initialization
 * function.
 */
fun savedState(init: (SavedState) -> Unit): SavedState {
    return SavedState().also(init)
}

/**
 * Retrieves the value for given [key] and tries to cast it as a [T].
 *
 * If the value for given [key] is not of type [T], `null` will be returned.
 */
inline operator fun <reified T : Any> SavedState.get(key: String): T? {
    return when (T::class) {
        Byte::class -> (getUnchecked(key) as? Number)?.toByte()
        Short::class -> (getUnchecked(key) as? Number)?.toShort()
        else -> getUnchecked(key)
    } as? T
}
