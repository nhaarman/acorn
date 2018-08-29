package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.state.ContainerState

/**
 * Indicates that implementers can have their instance state saved.
 */
interface SaveableContainer : Container {

    /**
     * Save instance state.
     */
    fun saveInstanceState(): ContainerState
}

/**
 * Indicates that implementers can have their instance state saved and restored.
 */
interface RestorableContainer : SaveableContainer {

    /** Restore given instance state. */
    fun restoreInstanceState(bundle: ContainerState)
}