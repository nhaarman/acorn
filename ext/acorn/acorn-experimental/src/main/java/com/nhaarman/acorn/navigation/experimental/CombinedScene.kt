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

package com.nhaarman.acorn.navigation.experimental

import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

/**
 * A [Scene] that combines two Scenes into one.
 * The key of this Scene is taken from [secondScene].
 *
 * This class is used in conjunction with [ConcurrentPairNavigator].
 */
@ExperimentalConcurrentPairNavigator
class CombinedScene(
    val firstScene: Scene<out Container>,
    val secondScene: Scene<out Container>
) : Scene<CombinedContainer> {

    override val key: SceneKey
        get() = secondScene.key

    override fun onStart() {
    }

    @Suppress("UNCHECKED_CAST")
    override fun attach(v: CombinedContainer) {
        (firstScene as Scene<Container>).attach(v.firstContainer)
        (secondScene as Scene<Container>).attach(v.secondContainer)
    }

    @Suppress("UNCHECKED_CAST")
    override fun detach(v: CombinedContainer) {
        (secondScene as Scene<Container>).detach(v.secondContainer)
        (firstScene as Scene<Container>).detach(v.firstContainer)
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }
}
