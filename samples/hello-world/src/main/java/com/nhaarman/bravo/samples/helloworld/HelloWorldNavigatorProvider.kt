package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.android.navigation.AbstractNavigatorProvider

object HelloWorldNavigatorProvider : AbstractNavigatorProvider<HelloWorldNavigator>() {

    override fun createNavigator(savedState: BravoBundle?): HelloWorldNavigator {
        return HelloWorldNavigator()
    }
}