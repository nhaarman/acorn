package com.nhaarman.bravo.presentation

import android.support.annotation.CallSuper
import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.BravoBundle.Companion.bundle
import com.nhaarman.bravo.StateRestorable
import com.nhaarman.bravo.StateSaveable

/**
 * A saveable [Scene] implementation that saves and restores view state between
 * different views, and implements [StateSaveable] to be able to save the
 * Scene itself.
 *
 * Calling [saveInstanceState] on this class will also save the view state,
 * if available.
 *
 * @param V The view type for this [Scene], must implement [StateRestorable].
 * @param savedState A previous saved state instance for this [Scene].
 * May be `null`.
 * @constructor Creates a new [SaveableScene], restoring view state when available.
 */
abstract class SaveableScene<V>(
    savedState: BravoBundle?
) : Scene<V>, StateSaveable
    where V : Container, V : StateRestorable {

    private var view: V? = null
    private var viewState: BravoBundle? = savedState?.viewState

    @CallSuper
    override fun attach(v: V) {
        viewState?.let { v.restoreInstanceState(it) }
        viewState = null
        view = v
    }

    @CallSuper
    override fun detach(v: V) {
        viewState = v.saveInstanceState()
        view = null
    }

    @CallSuper
    override fun saveInstanceState(): BravoBundle {
        return bundle {
            it.viewState = viewState ?: view?.saveInstanceState()
        }
    }

    companion object {

        private var BravoBundle.viewState: BravoBundle?
            set(value) {
                this["view_state"] = value
            }
            get() = get("view_state")
    }
}