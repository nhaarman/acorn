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

package com.nhaarman.acorn.samples.helloworld

import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.SingleSceneNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SceneState

/**
 * A [Navigator] that shows a single "Hello, world!" [Scene].
 *
 * This Navigator does not handle any state restoration, since there is no state
 * worth saving.
 */
class HelloWorldNavigator : SingleSceneNavigator(null) {

    override fun createScene(state: SceneState?): Scene<out Container> {
        return HelloWorldScene()
    }
}
