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

package com.nhaarman.bravo.android.internal

import android.support.annotation.AnyRes
import android.support.annotation.ColorInt
import android.util.TypedValue
import android.view.View

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
