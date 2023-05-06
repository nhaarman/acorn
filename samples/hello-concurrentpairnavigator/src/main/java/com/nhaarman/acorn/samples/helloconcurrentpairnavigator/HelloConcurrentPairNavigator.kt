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

package com.nhaarman.acorn.samples.helloconcurrentpairnavigator

import com.nhaarman.acorn.navigation.experimental.ConcurrentPairNavigator
import com.nhaarman.acorn.navigation.experimental.ExperimentalConcurrentPairNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SceneState
import kotlin.reflect.KClass

@OptIn(ExperimentalConcurrentPairNavigator::class)
class HelloConcurrentPairNavigator : ConcurrentPairNavigator(null) {

    override fun createInitialScene(): Scene<out Container> {
        return FirstScene(FirstSceneListener())
    }

    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            FirstScene::class -> FirstScene(FirstSceneListener())
            SecondScene::class -> SecondScene(SecondSceneListener())
            else -> error("Unknown scene: $sceneClass")
        }
    }

    inner class FirstSceneListener : FirstScene.Events {

        override fun actionClicked() {
            push(SecondScene(SecondSceneListener()))
        }
    }

    inner class SecondSceneListener : SecondScene.Events {

        override fun onBackClicked() {
            pop()
        }
    }
}
