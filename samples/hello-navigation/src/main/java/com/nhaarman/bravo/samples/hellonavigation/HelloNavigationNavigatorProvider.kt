package com.nhaarman.bravo.samples.hellonavigation

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.android.navigation.AbstractNavigatorProvider

object HelloNavigationNavigatorProvider : AbstractNavigatorProvider<HelloNavigationNavigator>() {

    override fun createNavigator(savedState: BravoBundle?): HelloNavigationNavigator {
        return HelloNavigationNavigator()
    }
}