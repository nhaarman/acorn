package com.nhaarman.bravo.presentation

import android.support.annotation.CallSuper
import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.StateRestorable

/**
 * An abstract [Scene] implementation that saves and restores view state between
 * different views, and provides a reference to the currently attached view.
 *
 * @param V The view type for this [Scene], must implement [StateRestorable].
 * @property viewState The initial view state for this [Scene].
 * @constructor Creates a new [BasicScene], restoring view state when available.
 */
abstract class BasicScene<V>
    : Scene<V> where V : Container, V : StateRestorable {

    private var viewState: BravoBundle? = null

    /**
     * The currently attached [V] instance, if available.
     * Returns `null` if no instance is attached.
     */
    protected var currentView: V? = null
        private set

    @CallSuper
    override fun attach(v: V) {
        viewState?.let { v.restoreInstanceState(it) }
        currentView = v
    }

    @CallSuper
    override fun detach(v: V) {
        viewState = v.saveInstanceState()
        currentView = null
    }
}