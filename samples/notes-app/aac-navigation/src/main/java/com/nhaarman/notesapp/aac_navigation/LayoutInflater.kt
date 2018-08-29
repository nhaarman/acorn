package com.nhaarman.notesapp.aac_navigation

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.inflate(@LayoutRes layoutResId: Int, attachToParent: Boolean = false): T {
    return LayoutInflater.from(context).inflate(layoutResId, this, attachToParent) as T
}