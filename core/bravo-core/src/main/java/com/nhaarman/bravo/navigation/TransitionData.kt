package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.presentation.Scene

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
