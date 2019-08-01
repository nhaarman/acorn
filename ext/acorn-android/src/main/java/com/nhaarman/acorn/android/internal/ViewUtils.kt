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

package com.nhaarman.acorn.android.internal

import android.util.TypedValue
import android.view.View
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt

/**
 * Applies the window background from the View's theme as this View's background.
 */
internal fun View.applyWindowBackground() {
    val value = TypedValue().also {
        context.theme.resolveAttribute(android.R.attr.windowBackground, it, true)
    }

    when {
        value.type == TypedValue.TYPE_NULL -> background = null
        value.type == TypedValue.TYPE_REFERENCE -> applyBackgroundReference(value.resourceId)
        value.type == TypedValue.TYPE_STRING -> applyBackgroundReference(value.resourceId)
        value.type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT -> applyBackgroundColor(value.data)
        else -> error("Unknown type: 0x${Integer.toHexString(value.type)}")
    }.let { /* Safe when let */ }
}

private fun View.applyBackgroundReference(@AnyRes resourceId: Int) {
    return when (resourceId) {
        0 -> background = null
        else -> setBackgroundResource(resourceId)
    }
}

private fun View.applyBackgroundColor(@ColorInt value: Int) {
    setBackgroundColor(value)
}
