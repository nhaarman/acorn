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

package com.nhaarman.acorn.android.transition

import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene

/**
 * An interface that can create [SceneTransition] instances to animate transitions
 * between [Scene]s.
 */
interface SceneTransitionFactory {

    /**
     * Returns `true` when this SceneTransitionFactory can create a [SceneTransition]
     * when [transitionFor] is called.
     * If this method returns false for a specific combination of its parameters,
     * no calls to [transitionFor] with that combination must be made.
     */
    fun supports(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Boolean

    /**
     * Creates a new [SceneTransition] for given [Scene]s.
     *
     * @param previousScene The Scene to start the animation from.
     * @param newScene The Scene to animate to.
     * @param data Optional data for the transition.
     */
    fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition
}
