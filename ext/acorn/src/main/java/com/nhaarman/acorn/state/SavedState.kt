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

import com.nhaarman.acorn.state.internal.BaseSavedState

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
