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

import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene

/**
 * A [SceneTransitionFactory] that uses the [TransitionData.isBackwards] flag to
 * determine the transition.
 */
class DefaultSceneTransitionFactory(
    private val viewControllerFactory: ViewControllerFactory
) : SceneTransitionFactory {

    override fun supports(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Boolean {
        return true
    }

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition {
        return when (data?.isBackwards) {
            true -> FadeOutToBottomTransition { parent ->
                viewControllerFactory.viewControllerFor(newScene, parent)
            }
            else -> FadeInFromBottomTransition { parent ->
                viewControllerFactory.viewControllerFor(newScene, parent)
            }
        }.hideKeyboardOnStart()
    }
}
