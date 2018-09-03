package com.nhaarman.bravo.android.navigation

import android.app.Activity
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.state.NavigatorState

/**
 * An interface that provides a [Navigator] instance to be used in the [Activity].
 *
 * Implementations generally need to cache a created Navigator to deliver the
 * same instance to multiple Activities.
 */
interface NavigatorProvider {

    /**
     * Returns the [Navigator] instance to work with.
     * If one needs to be created, given [savedState] can be used to restore
     * any state, if applicable.
     *
     * @param savedState If not null, the saved state for the Navigator as returned
     * by [saveNavigatorState].
     */
    fun navigatorFor(savedState: NavigatorState?): Navigator<*>

    /**
     * Returns the saved state for the [Navigator] as returned by [navigatorFor].
     * Implementations can add extra data to the resulting state to suit their
     * needs.
     */
    fun saveNavigatorState(): NavigatorState?
}
