/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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