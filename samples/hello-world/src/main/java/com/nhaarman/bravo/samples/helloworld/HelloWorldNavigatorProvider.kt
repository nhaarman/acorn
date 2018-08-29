package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.android.navigation.AbstractNavigatorProvider

object HelloWorldNavigatorProvider : AbstractNavigatorProvider<HelloWorldNavigator>() {

    override fun createNavigator(savedState: NavigatorState?): HelloWorldNavigator {
        return HelloWorldNavigator()
    }
}