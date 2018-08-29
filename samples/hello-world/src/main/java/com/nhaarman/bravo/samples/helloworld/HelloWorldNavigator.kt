package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.SingleSceneNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A [Navigator] that shows a single "Hello, world!" [Scene].
 *
 * This Navigator does not handle any state restoration, since there is no state
 * worth saving.
 */
class HelloWorldNavigator : SingleSceneNavigator<Navigator.Events>(null) {

    override fun createScene(state: SceneState?): Scene<out Container> {
        return HelloWorldScene()
    }
}