package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.state.NavigatorState

/**
 * Indicates that implementers can have their instance state saved.
 */
interface SaveableNavigator {

    /** Save instance state. */
    fun saveInstanceState(): NavigatorState
}
