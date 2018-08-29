package com.nhaarman.bravo.samples.hellostaterestoration

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.android.navigation.AbstractNavigatorProvider

object HelloStateRestorationNavigatorProvider : AbstractNavigatorProvider<HelloStateRestorationNavigator>() {

    override fun createNavigator(savedState: NavigatorState?): HelloStateRestorationNavigator {
        return HelloStateRestorationNavigator.create(savedState)
    }
}