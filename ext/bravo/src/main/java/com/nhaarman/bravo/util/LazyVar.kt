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

package com.nhaarman.bravo.util

import kotlin.reflect.KProperty

/**
 * Creates a new [LazyVar] implementation.
 *
 * @param init creates the lazily evaluated instance.
 */
fun <T> lazyVar(init: () -> T) = LazyVar(init)

/**
 * Represents a variable with lazy initialization.
 *
 * If the value is read before written, the initializer will be called.
 * If the value is written to before being read, the initializer will not be used.
 *
 * @see lazyVar
 */
class LazyVar<T> internal constructor(private val init: () -> T) {

    private var value: T? = null
        get() = synchronized(init) {
            if (field == null) {
                field = init()
            }

            return field
        }

    /** Retrieves the value. */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value!!
    }

    /** Sets the value. */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value!!
    }
}
