/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.android.transition

import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.nhaarman.acorn.presentation.Scene

/**
 * Returns a new [Transition] instance that hides the keyboard before the
 * receiving Transition is invoked.
 */
fun Transition.hideKeyboardOnStart(): Transition {
    return doOnStart(hideKeyboard)
}

fun ((Scene<*>) -> Transition).hideKeyboardOnStart(): (Scene<*>) -> Transition {
    return { scene: Scene<*> -> invoke(scene).doOnStart(hideKeyboard) }
}

private val hideKeyboard: (ViewGroup) -> Unit = { viewGroup ->
    (viewGroup
        .context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(viewGroup.windowToken, 0)
}
