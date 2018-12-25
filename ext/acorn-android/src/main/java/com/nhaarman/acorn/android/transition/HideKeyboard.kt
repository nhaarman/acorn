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
