package com.nhaarman.bravo.android.transition

import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.nhaarman.bravo.presentation.Scene

fun Transition.doOnStart(action: (ViewGroup) -> Unit): Transition = DoBeforeTransition(this, action)

fun ((Scene<*>) -> Transition).hideKeyboardOnStart() =
    { scene: Scene<*> ->
        invoke(scene).doOnStart { viewGroup ->
            (viewGroup
                .context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(viewGroup.windowToken, 0)
        }
    }

class DoBeforeTransition(
    private val delegate: Transition,
    private val action: (ViewGroup) -> Unit
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        action(parent)
        delegate.execute(parent, callback)
    }
}
