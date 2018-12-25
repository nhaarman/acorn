/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.samples.hellonavigation

import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.StackNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import kotlin.reflect.KClass

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
    StackNavigator(null),
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

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            FirstScene::class -> FirstScene(this)
            SecondScene::class -> SecondScene(this)
            else -> error("Unknown scene: $sceneClass")
        }
    }
}