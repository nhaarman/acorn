package com.nhaarman.bravo.samples.hellonavigation

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.android.navigation.AbstractNavigatorProvider

object HelloNavigationNavigatorProvider : AbstractNavigatorProvider<HelloNavigationNavigator>() {

    override fun createNavigator(savedState: NavigatorState?): HelloNavigationNavigator {
        return HelloNavigationNavigator()
    }
}