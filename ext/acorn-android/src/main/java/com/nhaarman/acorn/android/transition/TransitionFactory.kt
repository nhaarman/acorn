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

import com.nhaarman.bravo.navigation.TransitionData
import com.nhaarman.bravo.presentation.Scene

/**
 * An interface that can create [Transition] instances to animate transitions
 * between [Scene]s.
 */
interface TransitionFactory {

    /**
     * Creates a new [Transition] for given [Scene]s.
     *
     * @param previousScene The Scene to start the animation from.
     * @param newScene The Scene to animate to.
     * @param data Optional data for the transition.
     */
    fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Transition
}