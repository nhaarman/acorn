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

package com.nhaarman.acorn.util

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
