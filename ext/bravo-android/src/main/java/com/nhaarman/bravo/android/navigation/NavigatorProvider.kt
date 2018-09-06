/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

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
