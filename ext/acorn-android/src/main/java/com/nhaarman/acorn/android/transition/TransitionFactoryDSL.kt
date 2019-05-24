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
import com.nhaarman.acorn.android.transition.internal.BindingSceneTransitionFactory
import com.nhaarman.acorn.android.transition.internal.ClassBinding
import com.nhaarman.acorn.android.transition.internal.KeyBinding
import com.nhaarman.acorn.android.transition.internal.LazyClassBinding
import com.nhaarman.acorn.android.transition.internal.TransitionBinding
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import kotlin.reflect.KClass

/**
 * Entry point for the [SceneTransitionFactory] DSL.
 *
 * @see [SceneTransitionFactoryBuilder]
 */
fun sceneTransitionFactory(
    init: SceneTransitionFactoryBuilder.() -> Unit
): SceneTransitionFactory {
    return SceneTransitionFactoryBuilder().apply(init).build()
}

/**
 * Entry point for the [SceneTransitionFactory] DSL.
 *
 * @see [SceneTransitionFactoryBuilder]
 */
@Deprecated(
    "Use sceneTransitionFactory(SceneTransitionFactoryBuilder.() -> Unit) instead",
    ReplaceWith("sceneTransitionFactory(init)")
)
fun sceneTransitionFactory(
    viewControllerFactory: ViewControllerFactory,
    init: SceneTransitionFactoryBuilder.() -> Unit
): SceneTransitionFactory {
    return sceneTransitionFactory(init)
}

/**
 * A DSL that can create [SceneTransitionFactory] instances by binding pairs of Scenes
 * to [SceneTransition] instances.
 */
class SceneTransitionFactoryBuilder {

    private val bindings = mutableListOf<TransitionBinding>()

    /**
     * Binds two [SceneKey]s to a [SceneTransition] instance.
     */
    infix fun Pair<SceneKey, SceneKey>.use(transition: SceneTransition) {
        bindings += KeyBinding(first, second, transition)
    }

    /**
     * Binds two [Scene] classes to a [SceneTransition] instance.
     */
    @JvmName("useWithClasses")
    infix fun Pair<KClass<out Scene<*>>, KClass<out Scene<*>>>.use(transition: SceneTransition) {
        bindings += ClassBinding(first, second, transition)
    }

    /**
     * Binds two [Scene] classes to a lazily evaluated [SceneTransition] instance.
     *
     * @param transition A function that provides a [SceneTransition] instance. Its
     * parameter is the destination [Scene] of the SceneTransition.
     */
    infix fun Pair<KClass<out Scene<*>>, KClass<out Scene<*>>>.use(transition: (Scene<*>) -> SceneTransition) {
        bindings += LazyClassBinding(first, second, transition)
    }

    fun build(): SceneTransitionFactory {
        return BindingSceneTransitionFactory(bindings.asSequence())
    }
}
