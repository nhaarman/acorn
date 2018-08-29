package com.nhaarman.bravo.android.util

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflateView(@LayoutRes layoutResId: Int, attachToParent: Boolean = false): View {
    return inflate(layoutResId, attachToParent)
}

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.inflate(@LayoutRes layoutResId: Int, attachToParent: Boolean = false): T {
    return LayoutInflater.from(context).inflate(layoutResId, this, attachToParent) as T
}