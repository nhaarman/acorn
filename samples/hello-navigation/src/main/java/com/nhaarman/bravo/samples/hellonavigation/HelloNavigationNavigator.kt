/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.samples.hellonavigation

import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.StackNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * a [Navigator] that manages navigation between [FirstScene] and [SecondScene].
 *
 * This class extends [StackNavigator] which uses an internal stack to represent
 * the navigation state.
 *
 * This Navigator does not handle any state restoration, since there is no state
 * worth saving.
 */
class HelloNavigationNavigator :
// Extends StackNavigator to allow for pushing and popping Scenes of a stack.
    StackNavigator<Navigator.Events>(null),
    // Implements the callbacks for the Scene to execute Scene transitions.
    FirstScene.Events,
    SecondScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(FirstScene(this))
    }

    /**
     * Pushes a [SecondScene] on the stack.
     *
     * Calling [push] results in a notification to listeners of this Navigator
     * that the [Scene] has changed.
     */
    override fun secondSceneRequested() {
        push(SecondScene(this))
    }

    /**
     * Pops the [SecondScene] off the stack, showing [FirstScene].
     *
     * Calling [pop] results in a notification to listeners of this Navigator
     * that the [Scene] has changed.
     */
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