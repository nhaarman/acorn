package com.nhaarman.bravo.android.presentation

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import com.nhaarman.bravo.android.util.saveHierarchyState
import com.nhaarman.bravo.presentation.RestorableContainer
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.containerState

/**
 * A helper interface that offers default implementations for [View] state saving
 * and restoration.
 *
 * Classes that utilize the [LayoutContainer] interface and implement
 * [RestorableContainer] need to manually implement saving the hierarchy
 * state. To make this easier, you can implement this interface instead:
 *
 * ```
 * interface MyContainer: RestorableContainer
 *
 * class MyViewWrapper : LayoutContainer, MyContainer, RestorableLayoutContainer
 * ```
 */
interface RestorableLayoutContainer : RestorableContainer {

    val containerView: View

    override fun saveInstanceState() = containerState {
        it.hierarchyState = containerView.saveHierarchyState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        bundle.hierarchyState?.let { containerView.restoreHierarchyState(it) }
    }

    fun getHierarchyState(bundle: ContainerState): SparseArray<Parcelable>? {
        return bundle.hierarchyState
    }

    companion object {

        private var ContainerState.hierarchyState: SparseArray<Parcelable>?
            get() = get("hierarchy_state")
            set(value) {
                setUnchecked("hierarchy_state", value)
            }
    }
}