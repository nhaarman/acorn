package com.nhaarman.bravo.android.presentation

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.StateRestorable
import com.nhaarman.bravo.android.util.saveHierarchyState
import com.nhaarman.bravo.presentation.Container

/**
 * A helper interface that offers default implementations for [View] state saving
 * and restoration.
 *
 * Views that implement a [StateRestorable] [Container] need to manually
 * implement saving the hierarchy state. To make this easier, you can implement
 * this interface instead:
 *
 * ```
 * interface MyContainer: Container, StateRestorable
 *
 * class MyView : View(...), MyContainer, RestorableView
 * ```
 *
 * Note: Classes implementing this interface *must* also extend [View].
 */
interface RestorableView : StateRestorable {

    override fun saveInstanceState() = BravoBundle.bundle {
        it.hierarchyState = (this as View).saveHierarchyState()
    }

    override fun restoreInstanceState(bundle: BravoBundle) {
        bundle.hierarchyState?.let { (this as View).restoreHierarchyState(it) }
    }

    companion object {

        private var BravoBundle.hierarchyState: SparseArray<Parcelable>?
            get() = get("hierarchy_state")
            set(value) {
                this["hierarchy_state"] = value
            }
    }
}
