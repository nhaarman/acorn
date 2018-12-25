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

package com.nhaarman.acorn.android.util

import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SavedState
import com.nhaarman.acorn.state.SceneState
import java.io.Serializable
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Copyright (C) 2018 The Android Open Source Project
 */
private fun Bundle.put(key: String, value: Any?) {
    when (value) {
        null -> putString(key, null) // Any nullable type will suffice.

        // Scalars
        is Boolean -> putBoolean(key, value)
        is Byte -> putByte(key, value)
        is Char -> putChar(key, value)
        is Double -> putDouble(key, value)
        is Float -> putFloat(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Short -> putShort(key, value)

        // References
        is Bundle -> putBundle(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Parcelable -> putParcelable(key, value)

        // Scalar arrays
        is BooleanArray -> putBooleanArray(key, value)
        is ByteArray -> putByteArray(key, value)
        is CharArray -> putCharArray(key, value)
        is DoubleArray -> putDoubleArray(key, value)
        is FloatArray -> putFloatArray(key, value)
        is IntArray -> putIntArray(key, value)
        is LongArray -> putLongArray(key, value)
        is ShortArray -> putShortArray(key, value)

        // Reference arrays
        is Array<*> -> {
            val componentType = value::class.java.componentType!!
            @Suppress("UNCHECKED_CAST") // Checked by reflection.
            when {
                Parcelable::class.java.isAssignableFrom(componentType) -> {
                    putParcelableArray(key, value as Array<Parcelable>)
                }
                String::class.java.isAssignableFrom(componentType) -> {
                    putStringArray(key, value as Array<String>)
                }
                CharSequence::class.java.isAssignableFrom(componentType) -> {
                    putCharSequenceArray(key, value as Array<CharSequence>)
                }
                Serializable::class.java.isAssignableFrom(componentType) -> {
                    putSerializable(key, value)
                }
                else -> {
                    val valueType = componentType.canonicalName
                    throw IllegalArgumentException(
                        "Illegal value array type $valueType for key \"$key\""
                    )
                }
            }
        }

        // State types
        is NavigatorState -> put(key, bundleOf("type" to "navigator_state", "state" to value.toBundle()))
        is SceneState -> put(key, bundleOf("type" to "scene_state", "state" to value.toBundle()))
        is ContainerState -> put(key, bundleOf("type" to "container_state", "state" to value.toBundle()))
        is SavedState -> put(key, bundleOf("type" to "saved_state", "state" to value.toBundle()))

        // Last resort. Also we must check this after Array<*> as all arrays are serializable.
        is Serializable -> putSerializable(key, value)

        else -> {
            if (value is SparseArray<*>) {
                /* This is arguably a Bad Thing(TM), but we need this for view state saving. */
                @Suppress("UNCHECKED_CAST")
                putSparseParcelableArray(key, value as SparseArray<out Parcelable>)
            } else if (value is Binder) {
                putBinder(key, value)
            } else if (Build.VERSION.SDK_INT >= 21 && value is Size) {
                putSize(key, value)
            } else if (Build.VERSION.SDK_INT >= 21 && value is SizeF) {
                putSizeF(key, value)
            } else {
                val valueType = value.javaClass.canonicalName
                throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
            }
        }
    }
}

private fun bundleOf(vararg values: Pair<String, Any?>): Bundle {
    return Bundle()
        .also { b ->
            values.forEach { (key, value) ->
                b.put(key, value)
            }
        }
}

fun SavedState.toBundle(): Bundle {
    return Bundle(entries.size)
        .apply {
            entries.forEach { (key, value) ->
                put(key, value)
            }
        }
}

fun Bundle.toNavigatorState(): NavigatorState = NavigatorState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToAcorn(get(key))) }
}

private fun Bundle.toSceneState(): SceneState = SceneState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToAcorn(get(key))) }
}

private fun Bundle.toContainerState(): ContainerState = ContainerState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToAcorn(get(key))) }
}

private fun Bundle.toSavedState(): SavedState = SavedState().also { bundle ->
    keySet()
        .forEach { key -> bundle.setUnchecked(key, transformToAcorn(get(key))) }
}

private fun transformToAcorn(it: Any?): Any? {
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