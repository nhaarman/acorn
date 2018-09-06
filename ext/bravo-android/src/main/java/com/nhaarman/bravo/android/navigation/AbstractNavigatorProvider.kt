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

import com.nhaarman.bravo.android.internal.i
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.SaveableNavigator
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.get
import com.nhaarman.bravo.state.navigatorState
import java.util.concurrent.TimeUnit

/**
 * A [NavigatorProvider] that caches its created [Navigator] for as long as it
 * isn't destroyed.
 *
 * This instance uses a 30-minute timeout to determine whether to restore any
 * saved Navigator state. This means that if the saved state is older than 30
 * minutes, the state is dropped and a fresh instance is created.
 */
abstract class AbstractNavigatorProvider<N : Navigator<out Navigator.Events>> : NavigatorProvider {

    private var navigator: N? = null

    override fun navigatorFor(savedState: NavigatorState?): N {
        var result = navigator

        if (result == null || result.isDestroyed()) {
            result = createNavigatorInternal(savedState)
        }

        return result.also { this.navigator = it }
    }

    private fun createNavigatorInternal(savedState: NavigatorState?): N {
        if (savedState == null) return createNavigator(null)

        val timeMillis = savedState.timeMillis ?: return createNavigator(savedState.navigatorState)

        if (System.currentTimeMillis() - timeMillis < TimeUnit.MINUTES.toMillis(30)) {
            i(javaClass.simpleName, "Restoring from previous state.")
            return createNavigator(savedState.navigatorState)
        }

        i(javaClass.simpleName, "Dropping stale state: older than 30 minutes.")
        return createNavigator(null)
    }

    abstract fun createNavigator(savedState: NavigatorState?): N

    override fun saveNavigatorState(): NavigatorState? {
        return navigatorState {
            it.timeMillis = System.currentTimeMillis()
            it.navigatorState = (navigator as? SaveableNavigator)?.saveInstanceState()
        }
    }

    companion object {

        private var NavigatorState.timeMillis: Long?
            get() = get("timestamp")
            set(value) {
                set("timestamp", value)
            }

        private var NavigatorState.navigatorState: NavigatorState?
            get() = get("navigator")
            set(value) {
                set("navigator", value)
            }
    }
}