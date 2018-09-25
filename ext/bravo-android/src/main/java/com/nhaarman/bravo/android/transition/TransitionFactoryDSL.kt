/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.transition

import com.nhaarman.bravo.android.presentation.ViewFactory
import com.nhaarman.bravo.android.transition.internal.BindingTransitionFactory
import com.nhaarman.bravo.android.transition.internal.ClassBinding
import com.nhaarman.bravo.android.transition.internal.KeyBinding
import com.nhaarman.bravo.android.transition.internal.LazyClassBinding
import com.nhaarman.bravo.android.transition.internal.TransitionBinding
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey
import kotlin.reflect.KClass

/**
 * Entry point for the [TransitionFactory] DSL.
 *
 * @see [TransitionFactoryBuilder]
 */
fun transitionFactory(viewFactory: ViewFactory, init: TransitionFactoryBuilder.() -> Unit): TransitionFactory {
    return TransitionFactoryBuilder(viewFactory).apply(init).build()
}

/**
 * A DSL that can create [TransitionFactory] instances by binding pairs of Scenes
 * to [Transition] instances.
 *
 * @param viewFactory The [ViewFactory] instance to use for layout inflation for
 * fallback transition animations.
 */
class TransitionFactoryBuilder internal constructor(
    private val viewFactory: ViewFactory
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
            viewFactory,
            bindings.asSequence()
        )
    }
}
