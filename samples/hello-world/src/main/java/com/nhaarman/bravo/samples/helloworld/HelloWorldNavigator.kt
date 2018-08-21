package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.SingleSceneNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

class HelloWorldNavigator : SingleSceneNavigator<Navigator.Events>(null) {

    override fun createScene(state: BravoBundle?): Scene<out Container> {
        return HelloWorldScene()
    }
}