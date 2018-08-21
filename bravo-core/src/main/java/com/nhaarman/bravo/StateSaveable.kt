package com.nhaarman.bravo

/**
 * Indicates that implementers can have their instance state saved.
 */
interface StateSaveable {

    /**
     * Save instance state.
     */
    fun saveInstanceState(): BravoBundle
}