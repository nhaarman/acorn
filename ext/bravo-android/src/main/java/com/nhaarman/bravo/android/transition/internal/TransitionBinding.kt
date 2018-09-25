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

package com.nhaarman.bravo.android.transition.internal

import com.nhaarman.bravo.android.transition.Transition
import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.presentation.SceneKey
import kotlin.reflect.KClass

internal interface TransitionBinding {

    fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): Transition?
}

internal class KeyBinding(
    private val fromKey: SceneKey,
    private val toKey: SceneKey,
    private val transition: Transition
) : TransitionBinding {

    override fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): Transition? {
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
    private val transition: Transition
) : TransitionBinding {

    override fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): Transition? {
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
    private val transition: (Scene<*>) -> Transition
) : TransitionBinding {

    override fun transitionFor(fromScene: Scene<*>, toScene: Scene<*>, data: TransitionData?): Transition? {
        if (fromScene::class != fromClass) return null
        if (toScene::class != toClass) return null

        return transition.invoke(toScene)
    }

    override fun toString(): String {
        return "LazyClassBinding(fromClass=$fromClass, toClass=$toClass)"
    }
}
