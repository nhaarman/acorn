package com.nhaarman.bravo.presentation

import com.nhaarman.bravo.state.SceneState

/**
 * Indicates that implementers can have their instance state saved.
 */
interface SaveableScene {

    /**
     * Save instance state.
     */
    fun saveInstanceState(): SceneState
}