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

import com.nhaarman.acorn.android.transition.internal.TransitionCreationFailure
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene

/**
 * A [SceneTransitionFactory] implementation that can delegate to other implementations.
 */
class ComposingSceneTransitionFactory private constructor(
    private val sources: Sequence<SceneTransitionFactory>
) : SceneTransitionFactory {

    override fun supports(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Boolean {
        return sources.any { it.supports(previousScene, newScene, data) }
    }

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): SceneTransition {
        return sources
            .firstOrNull { it.supports(previousScene, newScene, data) }
            ?.transitionFor(previousScene, newScene, data)
            ?: throw TransitionCreationFailure(previousScene, newScene)
    }

    companion object {

        fun from(sources: List<SceneTransitionFactory>) = ComposingSceneTransitionFactory(sources.asSequence())
        fun from(vararg sources: SceneTransitionFactory) = ComposingSceneTransitionFactory(sources.asSequence())
    }
}
