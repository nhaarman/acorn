package com.nhaarman.bravo.android.util

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.os.bundleOf
import com.nhaarman.bravo.BravoBundle

fun BravoBundle.toBundle(): Bundle {
    return entries
        .filter { (_, value) -> value !is SparseArray<*> }
        .map { (key, value) ->
            val v = if (value is BravoBundle) {
                value.toBundle()
            } else {
                value
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

fun Bundle.toBundle(): BravoBundle = BravoBundle().also { bundle ->
    keySet()
        .forEach { key ->
            bundle[key] = get(key).let {
                when (it) {
                    is Bundle -> it.toBundle()
                    else -> it
                }
            }
        }
}

fun Bundle.getBravoBundle(key: String) = getBundle(key)?.toBundle()

fun Bundle.putBravoBundle(key: String, bundle: BravoBundle?) = putBundle(key, bundle?.toBundle())