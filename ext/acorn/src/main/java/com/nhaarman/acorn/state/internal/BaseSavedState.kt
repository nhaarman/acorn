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

package com.nhaarman.acorn.state.internal

import com.nhaarman.acorn.state.SavedState

/**
 * Provides a base implementation for the [SavedState] interface.
 */
internal class BaseSavedState(
    private val map: MutableMap<String, Any?> = mutableMapOf()
) : SavedState {

    override val entries: Set<Map.Entry<String, Any?>>
        get() {
            return map.entries
        }

    override fun clear(key: String) {
        map[key] = null
    }

    override fun set(key: String, value: Boolean?) {
        map[key] = value
    }

    override fun set(key: String, value: Number?) {
        map[key] = value
    }

    override fun set(key: String, value: Char?) {
        map[key] = value
    }

    override fun set(key: String, value: String?) {
        map[key] = value
    }

    override fun set(key: String, value: SavedState?) {
        map[key] = value
    }

    override fun setUnchecked(key: String, value: Any?) {
        map[key] = value
    }

    override fun getUnchecked(key: String): Any? {
        return map[key]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseSavedState

        if (map != other.map) return false

        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return "$map"
    }
}