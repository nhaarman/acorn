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

import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import com.nhaarman.bravo.state.ContainerState
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SavedState
import com.nhaarman.bravo.state.SceneState
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