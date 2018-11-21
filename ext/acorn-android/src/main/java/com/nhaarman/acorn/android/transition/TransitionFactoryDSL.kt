/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.android.transition

import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.internal.BindingTransitionFactory
import com.nhaarman.acorn.android.transition.internal.ClassBinding
import com.nhaarman.acorn.android.transition.internal.KeyBinding
import com.nhaarman.acorn.android.transition.internal.LazyClassBinding
import com.nhaarman.acorn.android.transition.internal.TransitionBinding
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import kotlin.reflect.KClass

/**
 * Entry point for the [TransitionFactory] DSL.
 *
 * @see [TransitionFactoryBuilder]
 */
fun transitionFactory(
    viewControllerFactory: ViewControllerFactory,
    init: TransitionFactoryBuilder.() -> Unit
): TransitionFactory {
    return TransitionFactoryBuilder(viewControllerFactory).apply(init).build()
}

/**
 * A DSL that can create [TransitionFactory] instances by binding pairs of Scenes
 * to [Transition] instances.
 *
 * @param viewControllerFactory The [ViewControllerFactory] instance to use for
 * layout inflation for fallback transition animations.
 */
class TransitionFactoryBuilder internal constructor(
    private val viewControllerFactory: ViewControllerFactory
) {

    private val bindings = mutableListOf<TransitionBinding>()

    /**
     * Binds two [SceneKey]s to a [Transition] instance.
     */
    infix fun Pair<SceneKey, SceneKey>.use(transition: Transition) {
        bindings += KeyBinding(first, second, transition)
    }

    /**
     * Binds two [Scene] classes to a [Transition] instance.
     */
    @JvmName("useWithClasses")
    infix fun Pair<KClass<out Scene<*>>, KClass<out Scene<*>>>.use(transition: Transition) {
        bindings += ClassBinding(first, second, transition)
    }

    /**
     * Binds two [Scene] classes to a lazily evaluated [Transition] instance.
     *
     * @param transition A function that provides a [Transition] instance. Its
     * parameter is the destination [Scene] of the Transition.
     */
    infix fun Pair<KClass<out Scene<*>>, KClass<out Scene<*>>>.use(transition: (Scene<*>) -> Transition) {
        bindings += LazyClassBinding(first, second, transition)
    }

    fun build(): TransitionFactory {
        return BindingTransitionFactory(
            viewControllerFactory,
            bindings.asSequence()
        )
    }
}
