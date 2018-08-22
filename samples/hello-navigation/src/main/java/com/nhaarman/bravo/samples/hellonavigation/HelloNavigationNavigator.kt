package com.nhaarman.bravo.samples.hellonavigation

import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.StackNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

class HelloNavigationNavigator :
/** Extends StackNavigator to allow for pushing and popping Scenes of a stack. */
    StackNavigator<Navigator.Events>(null),
    /** Implements the callbacks for the Scene to execute Scene transitions. */
    FirstScene.Events,
    SecondScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(FirstScene(this))
    }

    override fun secondSceneRequested() {
        push(SecondScene(this))
    }

    override fun onFirstSceneRequested() {
        pop()
    }

    override fun instantiateScene(sceneClass: Class<Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            FirstScene::class.java -> FirstScene(this)
            SecondScene::class.java -> SecondScene(this)
            else -> error("Unknown scene: $sceneClass")
        }
    }
}