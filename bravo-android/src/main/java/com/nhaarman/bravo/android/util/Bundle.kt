package com.nhaarman.bravo.android.util

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.os.bundleOf
import androidx.core.util.component1
import androidx.core.util.component2
import com.nhaarman.bravo.ContainerState
import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.SavedState
import com.nhaarman.bravo.SceneState

fun SavedState.toBundle(): Bundle {
    return entries
        .filter { (_, value) -> value !is SparseArray<*> }
        .map { (key, value) ->

            val v = when (value) {
                is NavigatorState -> bundleOf("type" to "navigator_state", "state" to value.toBundle())
                is SceneState -> bundleOf("type" to "scene_state", "state" to value.toBundle())
                is ContainerState -> bundleOf("type" to "container_state", "state" to value.toBundle())
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

private fun transformToBravo(it: Any?): Any? {
    return when (it) {
        is Bundle -> {
            val bundleKey = it["type"]
            when (bundleKey) {
                "navigator_state" -> it.getBundle("state")?.toNavigatorState()
                "scene_state" -> it.getBundle("state")?.toSceneState()
                "container_state" -> it.getBundle("state")?.toContainerState()
                else -> it
            }
        }
        else -> it
    }
}