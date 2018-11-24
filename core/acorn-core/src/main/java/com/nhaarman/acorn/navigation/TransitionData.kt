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

package com.nhaarman.acorn.navigation

import com.nhaarman.acorn.presentation.Scene

/**
 * A data class that carries properties about a [Scene] transition.
 */
class TransitionData private constructor(
    /**
     * Denoted whether the transition is a transition that goes back to a
     * previous [Scene].
     *
     * @return `true` if the transition is backwards.
     */
    val isBackwards: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransitionData

        if (isBackwards != other.isBackwards) return false

        return true
    }

    override fun hashCode(): Int {
        return isBackwards.hashCode()
    }

    override fun toString(): String {
        return "TransitionData(isBackwards=$isBackwards)"
    }

    companion object {

        val forwards = create(isBackwards = false)
        val backwards = create(isBackwards = true)

        fun create(isBackwards: Boolean): TransitionData {
            return TransitionData(isBackwards)
        }
    }
}
