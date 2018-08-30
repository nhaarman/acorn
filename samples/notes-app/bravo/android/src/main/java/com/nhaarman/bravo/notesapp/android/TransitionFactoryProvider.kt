package com.nhaarman.bravo.notesapp.android

import com.nhaarman.bravo.android.transition.DefaultTransitionFactory
import com.nhaarman.bravo.android.transition.TransitionFactory

object TransitionFactoryProvider {

    val transitionFactory: TransitionFactory by lazy {
        DefaultTransitionFactory(ViewFactoryProvider.viewFactory)
    }
}