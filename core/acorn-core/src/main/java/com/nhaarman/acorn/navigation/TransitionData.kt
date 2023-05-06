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
    val isBackwards: Boolean,
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
