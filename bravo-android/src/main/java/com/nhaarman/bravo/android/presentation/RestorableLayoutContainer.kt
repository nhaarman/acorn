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
 * Classes that utilize the [LayoutContainer] interface and implement a
 * [StateRestorable] [Container] need to manually implement saving the hierarchy
 * state. To make this easier, you can implement this interface instead:
 *
 * ```
 * interface MyContainer: Container, StateRestorable
 *
 * class MyViewWrapper : LayoutContainer, MyContainer, RestorableLayoutContainer
 * ```
 */
interface RestorableLayoutContainer : StateRestorable {

    val containerView: View

    override fun saveInstanceState() = BravoBundle.bundle {
        it.hierarchyState = containerView.saveHierarchyState()
    }

    override fun restoreInstanceState(bundle: BravoBundle) {
        bundle.hierarchyState?.let { containerView.restoreHierarchyState(it) }
    }

    fun getHierarchyState(bundle: BravoBundle): SparseArray<Parcelable>? {
        return bundle.hierarchyState
    }

    companion object {

        private var BravoBundle.hierarchyState: SparseArray<Parcelable>?
            get() = get("hierarchy_state")
            set(value) {
                this["hierarchy_state"] = value
            }
    }
}