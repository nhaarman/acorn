package com.nhaarman.bravo.android.transition

import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.nhaarman.bravo.presentation.Scene

fun Transition.hideKeyboardOnStart(): Transition {
    return doOnStart(hideKeyboard)
}

fun ((Scene<*>) -> Transition).hideKeyboardOnStart() =
    { scene: Scene<*> -> invoke(scene).doOnStart(hideKeyboard) }

private val hideKeyboard: (ViewGroup) -> Unit = { viewGroup ->
    (viewGroup
        .context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(viewGroup.windowToken, 0)
}
