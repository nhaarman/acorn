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

package com.nhaarman.acorn.android.transition.internal

import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import kotlin.reflect.KClass

internal interface TransitionBinding {

    fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): SceneTransition?
}

internal class KeyBinding(
    private val fromKey: SceneKey,
    private val toKey: SceneKey,
    private val transition: SceneTransition
) : TransitionBinding {

    override fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): SceneTransition? {
        if (fromScene.key != fromKey) return null
        if (toScene.key != toKey) return null

        return transition
    }

    override fun toString(): String {
        return "KeyBinding(fromKey=$fromKey, toKey=$toKey, transition=${transition::class})"
    }
}

internal class ClassBinding(
    private val fromClass: KClass<out Scene<*>>,
    private val toClass: KClass<out Scene<*>>,
    private val transition: SceneTransition
) : TransitionBinding {

    override fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): SceneTransition? {
        if (fromScene::class != fromClass) return null
        if (toScene::class != toClass) return null

        return transition
    }

    override fun toString(): String {
        return "ClassBinding(fromClass=$fromClass, toClass=$toClass, transition=${transition::class})"
    }
}

internal class LazyClassBinding(
    private val fromClass: KClass<out Scene<*>>,
    private val toClass: KClass<out Scene<*>>,
    private val transition: (Scene<*>) -> SceneTransition
) : TransitionBinding {

    override fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): SceneTransition? {
        if (fromScene::class != fromClass) return null
        if (toScene::class != toClass) return null

        return transition.invoke(toScene)
    }

    override fun toString(): String {
        return "LazyClassBinding(fromClass=$fromClass, toClass=$toClass)"
    }
}
