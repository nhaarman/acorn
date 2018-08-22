package com.nhaarman.bravo.android.navigation

import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.NavigatorState.Companion.navigatorState
import com.nhaarman.bravo.navigation.SaveableNavigator
import com.nhaarman.bravo.android.internal.i
import com.nhaarman.bravo.navigation.Navigator
import java.util.concurrent.TimeUnit

interface NavigatorProvider {

    fun navigatorFor(savedState: NavigatorState?): Navigator<*>

    fun saveNavigatorState(): NavigatorState?
}

abstract class AbstractNavigatorProvider<N : Navigator<*>> : NavigatorProvider {

    private var navigator: N? = null

    override fun navigatorFor(savedState: NavigatorState?): N {
        var result = navigator

        if (result == null) {
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