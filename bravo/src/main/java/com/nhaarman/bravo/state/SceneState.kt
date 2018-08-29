package com.nhaarman.bravo.state

import com.nhaarman.bravo.state.internal.BaseSavedState

/**
 * Creates a new [SceneState] instance.
 */
fun SceneState(): SceneState {
    return DefaultSceneState(BaseSavedState())
}

/**
 * Creates a new [SceneState] instance, providing a DSL-like initialization
 * function.
 */
fun sceneState(init: (SceneState) -> Unit): SceneState {
    return SceneState().also(init)
}

private data class DefaultSceneState(
    private val delegate: SavedState
) : SceneState, SavedState by delegate {

    override fun set(key: String, value: ContainerState?) {
        setUnchecked(key, value)
    }
}
