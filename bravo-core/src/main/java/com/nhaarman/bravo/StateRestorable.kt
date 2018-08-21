package com.nhaarman.bravo

/**
 * Indicates that implementers can have their instance state saved and restored.
 */
interface StateRestorable : StateSaveable {

    /**
     * Restore given instance state.
     */
    fun restoreInstanceState(bundle: BravoBundle)
}