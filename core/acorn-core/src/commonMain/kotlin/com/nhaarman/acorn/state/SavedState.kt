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

package com.nhaarman.acorn.state

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