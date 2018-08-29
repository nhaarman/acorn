package com.nhaarman.bravo.android.transition

import com.nhaarman.bravo.presentation.Scene

interface TransitionFactory {

    fun transitionFor(previousScene: Scene<*>, newScene: Scene<*>): Transition
}