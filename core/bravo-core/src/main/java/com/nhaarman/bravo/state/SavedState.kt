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

/**
 * Provides a container to be able to save state.
 *
 * This interface uses a key-value strategy to store state.
 */
interface SavedState {

    /**
     * The set of entries that were saved in this container.
     */
    val entries: Set<Map.Entry<String, Any?>>

    /**
     * Clears the value for given [key].
     */
    fun clear(key: String)

    /**
     * Sets a boolean value for given [key].
     */
    operator fun set(key: String, value: Boolean?)

    /**
     * Sets a number value for given [key].
     */
    operator fun set(key: String, value: Number?)

    /**
     * Sets a char value for given [key].
     */
    operator fun set(key: String, value: Char?)

    /**
     * Sets a String value for given [key].
     */
    operator fun set(key: String, value: String?)

    /**
     * Sets a SavedState value for given [key].
     */
    operator fun set(key: String, value: SavedState?)

    /**
     * Sets any value for given [key].
     *
     * This method should be used with caution, as value types generally need
     * to be serializable in some form.
     * Failure to do so may cause in an Exception being thrown.
     */
    fun setUnchecked(key: String, value: Any?)

    /**
     * Retrieves the value for given [key].
     */
    fun getUnchecked(key: String): Any?
}