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
