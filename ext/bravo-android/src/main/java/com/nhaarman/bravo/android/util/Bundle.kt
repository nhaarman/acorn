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

package com.nhaarman.bravo.android.util

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.os.bundleOf
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SavedState
import com.nhaarman.bravo.state.SceneState
import kotlin.collections.component1
import kotlin.collections.component2

fun SavedState.toBundle(): Bundle {
    return entries
        .filter { (_, value) -> value !is SparseArray<*> }
        .map { (key, value) ->

            val v = when (value) {
                is NavigatorState -> bundleOf("type" to "navigator_state", "state" to value.toBundle())
                is SceneState -> bundleOf("type" to "scene_state", "state" to value.toBundle())
                is ContainerState -> bundleOf("type" to "container_state", "state" to value.toBundle())
                is SavedState -> bundleOf("type" to "saved_state", "state" to value.toBundle())
                else -> value
            }

            key to v
        }
        .toTypedArray()
        .let(::bundleOf)
        .also { bundle ->

            entries
                .filter { (_, value) -> value is SparseArray<*> }
                .forEach { (key, value) ->
                    /* This is arguably a Bad Thing(TM), but we need this for view state saving. */
                    @Suppress("UNCHECKED_CAST")
                    bundle.putSparseParcelableArray(key, value as SparseArray<out Parcelable>)
                }
        }
}

fun Bundle.toNavigatorState(): NavigatorState = NavigatorState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToBravo(get(key))) }
}

private fun Bundle.toSceneState(): SceneState = SceneState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToBravo(get(key))) }
}

private fun Bundle.toContainerState(): ContainerState = ContainerState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToBravo(get(key))) }
}

private fun Bundle.toSavedState(): SavedState = SavedState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToBravo(get(key))) }
}

private fun transformToBravo(it: Any?): Any? {
    return when (it) {
        is Bundle -> {
            val bundleKey = it["type"]
            when (bundleKey) {
                "navigator_state" -> it.getBundle("state")?.toNavigatorState()
                "scene_state" -> it.getBundle("state")?.toSceneState()
                "container_state" -> it.getBundle("state")?.toContainerState()
                "saved_state" -> it.getBundle("state")?.toSavedState()
                else -> it
            }
        }
        else -> it
    }
}