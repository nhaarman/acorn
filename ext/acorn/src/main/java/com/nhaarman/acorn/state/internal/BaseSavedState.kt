/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
