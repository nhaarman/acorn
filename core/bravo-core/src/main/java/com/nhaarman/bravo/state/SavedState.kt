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