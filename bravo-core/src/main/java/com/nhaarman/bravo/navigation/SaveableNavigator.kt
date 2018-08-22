package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.NavigatorState

/**
 * Indicates that implementers can have their instance state saved.
 */
interface SaveableNavigator {

    /** Save instance state. */
    fun saveInstanceState(): NavigatorState
}

/**
 * Indicates that implementers can have their instance state saved and restored.
 */
interface RestorableNavigator : SaveableNavigator {

    /** Restore given instance state. */
    fun restoreInstanceState(bundle: NavigatorState)
}