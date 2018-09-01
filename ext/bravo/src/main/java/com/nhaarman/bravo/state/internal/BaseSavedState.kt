package com.nhaarman.bravo.state.internal

import com.nhaarman.bravo.state.SavedState

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