/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.presentation

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import com.nhaarman.bravo.android.util.saveHierarchyState
import com.nhaarman.bravo.presentation.RestorableContainer
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.containerState
import com.nhaarman.bravo.state.get

/**
 * A helper interface that offers default implementations for [View] state saving
 * and restoration.
 *
 * Views that implement a [RestorableContainer] need to manually
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
interface RestorableView : RestorableContainer {

    override fun saveInstanceState() = containerState {
        it.hierarchyState = (this as View).saveHierarchyState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        bundle.hierarchyState?.let { (this as View).restoreHierarchyState(it) }
    }

    companion object {

        private var ContainerState.hierarchyState: SparseArray<Parcelable>?
            get() = get("hierarchy_state")
            set(value) {
                setUnchecked("hierarchy_state", value)
            }
    }
}
