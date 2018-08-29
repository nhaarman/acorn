package com.nhaarman.bravo.state

import com.nhaarman.bravo.state.internal.BaseSavedState

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
    private val delegate: SavedState
) : NavigatorState, SavedState by delegate {

    override fun set(key: String, value: SceneState?) {
        setUnchecked(key, value)
    }

    override fun set(key: String, value: NavigatorState?) {
        setUnchecked(key, value)
    }
}
