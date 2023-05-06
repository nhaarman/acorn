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

package com.nhaarman.acorn.samples.hellostartactivity

import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.StackNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
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
class HelloStartActivityNavigator(
    savedState: NavigatorState?,
) : StackNavigator(savedState),
    FirstScene.Events,
    AppSettingsScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(FirstScene(this))
    }

    override fun settingsRequested() {
        push(AppSettingsScene(this))
    }

    override fun settingsFinished() {
        pop()
    }

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            FirstScene::class -> FirstScene(this)
            AppSettingsScene::class -> AppSettingsScene(this)
            else -> error("Unknown scene: $sceneClass")
        }
    }
}
