package com.nhaarman.bravo.state

import com.nhaarman.bravo.state.internal.BaseSavedState

/**
 * Creates a new [ContainerState] instance.
 */
fun ContainerState(): ContainerState {
    return DefaultContainerState(BaseSavedState())
}

/**
 * Creates a new [ContainerState] instance, providing a DSL-like initialization
 * function.
 */
fun containerState(init: (ContainerState) -> Unit): ContainerState {
    return ContainerState().also(init)
}

private data class DefaultContainerState(
    private val delegate: SavedState
) : ContainerState, SavedState by delegate