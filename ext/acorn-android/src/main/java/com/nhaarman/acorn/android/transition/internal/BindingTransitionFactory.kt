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

package com.nhaarman.acorn.android.transition.internal

import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.DefaultTransitionFactory
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.transition.TransitionFactory
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene

internal class BindingTransitionFactory(
    private val viewControllerFactory: ViewControllerFactory,
    private val bindings: Sequence<TransitionBinding>
) : TransitionFactory {

    private val delegate by lazy { DefaultTransitionFactory(viewControllerFactory) }

    override fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Transition {
        return bindings
            .mapNotNull { it.transitionFor(previousScene, newScene, data) }
            .firstOrNull()
            ?: delegate.transitionFor(previousScene, newScene, data)
    }
}