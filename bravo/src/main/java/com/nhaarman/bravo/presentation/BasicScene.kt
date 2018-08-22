package com.nhaarman.bravo.presentation

import android.support.annotation.CallSuper
import com.nhaarman.bravo.ContainerState

/**
 * An abstract [Scene] implementation that saves and restores view state between
 * different views, and provides a reference to the currently attached view.
 *
 * @param V The view type for this [Scene], must implement [RestorableContainer].
 * @property containerState The initial view state for this [Scene].
 * @constructor Creates a new [BasicScene], restoring view state when available.
 */
abstract class BasicScene<V : RestorableContainer> : Scene<V> {

    private var containerState: ContainerState? = null

    /**
     * The currently attached [V] instance, if available.
     * Returns `null` if no instance is attached.
     */
    protected var currentView: V? = null
        private set

    @CallSuper
    override fun attach(v: V) {
        containerState?.let { v.restoreInstanceState(it) }
        currentView = v
    }

    @CallSuper
    override fun detach(v: V) {
        containerState = v.saveInstanceState()
        currentView = null
    }
}