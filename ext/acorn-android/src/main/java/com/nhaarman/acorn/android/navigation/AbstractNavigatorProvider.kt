/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.android.navigation

import com.nhaarman.acorn.android.internal.i
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.SavableNavigator
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.navigatorState
import java.util.concurrent.TimeUnit

/**
 * A [NavigatorProvider] that caches its created [Navigator] for as long as it
 * isn't destroyed.
 *
 * This instance uses a 30-minute timeout to determine whether to restore any
 * saved Navigator state. This means that if the saved state is older than 30
 * minutes, the state is dropped and a fresh instance is created.
 */
abstract class AbstractNavigatorProvider<N : Navigator> : NavigatorProvider {

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
            it.navigatorState = (navigator as? SavableNavigator)?.saveInstanceState()
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