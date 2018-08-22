package com.nhaarman.bravo.android.util

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.os.bundleOf
import com.nhaarman.bravo.ContainerState
import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.SavedState
import com.nhaarman.bravo.SceneState

fun SavedState.toBundle(): Bundle {
    return entries
        .filter { (_, value) -> value !is SparseArray<*> }
        .map { (key, value) ->

            val v = when (value) {
                is NavigatorState -> "navigator_state" to value.toBundle()
                is SceneState -> "scene_state" to value.toBundle()
                is ContainerState -> "container_state" to value.toBundle()
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
        is Pair<*, *> -> {
            val (bundleKey, bundleValue) = it
            if (bundleKey is String && bundleValue is Bundle) {
                when (bundleKey) {
                    "navigator_state" -> bundleValue.toNavigatorState()
                    "scene_state" -> bundleValue.toSceneState()
                    "container_state" -> bundleValue.toContainerState()
                    else -> it
                }
            } else {
                it
            }
        }
        else -> it
    }
}