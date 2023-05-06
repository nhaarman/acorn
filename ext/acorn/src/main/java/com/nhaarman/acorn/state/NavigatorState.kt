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
 * Creates a new [NavigatorState] instance.
 */
fun NavigatorState(): NavigatorState {
    return DefaultNavigatorState(BaseSavedState())
}

/**
 * Creates a new [NavigatorState] instance, providing a DSL-like initialization
 * function.
 */
fun navigatorState(init: (NavigatorState) -> Unit): NavigatorState {
    return NavigatorState().also(init)
}

private data class DefaultNavigatorState(
    private val delegate: SavedState,
) : NavigatorState, SavedState by delegate {

    override fun set(key: String, value: SceneState?) {
        setUnchecked(key, value)
    }

    override fun set(key: String, value: NavigatorState?) {
        setUnchecked(key, value)
    }
}
