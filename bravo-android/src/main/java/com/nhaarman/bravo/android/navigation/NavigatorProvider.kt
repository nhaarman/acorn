package com.nhaarman.bravo.android.navigation

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.StateSaveable
import com.nhaarman.bravo.android.internal.i
import com.nhaarman.bravo.navigation.Navigator
import java.util.concurrent.TimeUnit

interface NavigatorProvider {

    fun navigatorFor(savedState: BravoBundle?): Navigator<*>

    fun saveNavigatorState(): BravoBundle?
}

abstract class AbstractNavigatorProvider<N : Navigator<*>> : NavigatorProvider {

    private var navigator: N? = null

    override fun navigatorFor(savedState: BravoBundle?): N {
        var result = navigator

        if (result == null) {
            result = createNavigatorInternal(savedState)
        }

        return result.also { this.navigator = it }
    }

    private fun createNavigatorInternal(savedState: BravoBundle?): N {
        if (savedState == null) return createNavigator(null)

        val timeMillis = savedState.timeMillis ?: return createNavigator(savedState.navigatorState)

        if (System.currentTimeMillis() - timeMillis < TimeUnit.MINUTES.toMillis(30)) {
            i(javaClass.simpleName, "Restoring from previous state.")
            return createNavigator(savedState.navigatorState)
        }

        i(javaClass.simpleName, "Dropping stale state: older than 30 minutes.")
        return createNavigator(null)
    }

    abstract fun createNavigator(savedState: BravoBundle?): N

    override fun saveNavigatorState(): BravoBundle? {
        return BravoBundle.bundle {
            it.timeMillis = System.currentTimeMillis()
            it.navigatorState = (navigator as? StateSaveable)?.saveInstanceState()
        }
    }

    companion object {

        private var BravoBundle.timeMillis: Long?
            get() = get("timestamp")
            set(value) {
                set("timestamp", value)
            }

        private var BravoBundle.navigatorState: BravoBundle?
            get() = get("navigator")
            set(value) {
                set("navigator", value)
            }
    }
}