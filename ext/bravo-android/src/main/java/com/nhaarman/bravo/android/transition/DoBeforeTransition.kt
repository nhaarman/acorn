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

import android.view.ViewGroup

/**
 * A [Transition] implementation that can execute an action before a delegate
 * Transition is executed.
 */
class DoBeforeTransition private constructor(
    private val delegate: Transition,
    private val action: (ViewGroup) -> Unit
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        action(parent)
        delegate.execute(parent, callback)
    }

    companion object {

        fun create(delegate: Transition, action: (parent: ViewGroup) -> Unit): DoBeforeTransition {
            return DoBeforeTransition(delegate, action)
        }
    }
}

/**
 * Returns a [Transition] that runs [action] before the receiving Transition
 * instance is started.
 *
 * @param action The action to run.
 */
fun Transition.doOnStart(action: (parent: ViewGroup) -> Unit): Transition {
    return DoBeforeTransition.create(this, action)
}