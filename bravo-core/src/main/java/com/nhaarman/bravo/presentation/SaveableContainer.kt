package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.ContainerState

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
 *
 * [android.view.View] implementations can additionally implement
 * [com.nhaarman.bravo.presentation.RestorableView] which provides default
 * implementations of [saveInstanceState] and [restoreInstanceState].
 */
interface RestorableContainer : SaveableContainer {

    /** Restore given instance state. */
    fun restoreInstanceState(bundle: ContainerState)
}