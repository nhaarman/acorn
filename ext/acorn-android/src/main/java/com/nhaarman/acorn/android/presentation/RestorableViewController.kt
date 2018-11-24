/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.android.presentation

import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import com.nhaarman.acorn.android.util.saveHierarchyState
import com.nhaarman.acorn.presentation.RestorableContainer
import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.containerState
import com.nhaarman.acorn.state.get
import kotlinx.android.extensions.LayoutContainer

/**
 * A helper interface that offers default implementations for [View] state saving
 * and restoration.
 *
 * Classes that utilize the [ViewController] interface and implement
 * [RestorableContainer] need to manually implement saving the hierarchy
 * state. To make this easier, you can implement this interface instead:
 *
 * ```
 * interface MyContainer: RestorableContainer
 *
 * class MyViewController : MyContainer, RestorableViewController
 * ```
 */
interface RestorableViewController
    : ViewController, RestorableContainer, LayoutContainer {

    /**
     * This property is included from the [LayoutContainer] interface.
     * To ensure a uniform API, we hide this property.
     */
    @Deprecated("Use view instead", level = DeprecationLevel.HIDDEN)
    override val containerView: View?
        get() = view

    /**
     * A handle to the [Context] the [view] is running in.
     * @see [View.getContext].
     */
    val context: Context
        get() = view.context

    /**
     * A handle to the [Resources] associated to the [view].
     * @see [View.getResources].
     */
    val resources: Resources
        get() = view.resources

    override fun saveInstanceState() = containerState {
        it.hierarchyState = view.saveHierarchyState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        bundle.hierarchyState?.let { view.restoreHierarchyState(it) }
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