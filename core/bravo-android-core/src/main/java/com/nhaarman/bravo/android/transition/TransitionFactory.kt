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