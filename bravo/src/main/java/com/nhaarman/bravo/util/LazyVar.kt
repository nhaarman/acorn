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
