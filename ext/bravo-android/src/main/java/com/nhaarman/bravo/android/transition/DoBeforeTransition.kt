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