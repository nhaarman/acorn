package com.nhaarman.bravo.android.transition

import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import com.nhaarman.bravo.android.R

class FadeInFromBottomTransition(
    private val view: (ViewGroup) -> ViewResult
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val originalChildren = parent.children.asIterable().toList()

        val newViewResult = view(parent)

        val newView = newViewResult.view
        parent.addView(newView)
        val shouldClearBackground = newView.applyWindowBackground()

        newView.apply {
            translationY = parent.height / 5f
            translationZ = parent.resources.getDimension(R.dimen.bravo_fadeinfrombottomtransition_translationz)
            alpha = 0f
        }

        callback.attach(newViewResult)

        parent.doOnPreDraw {
            newView.animate()
                .alpha(1f)
                .translationY(0f)
                .setInterpolator(LinearOutSlowInInterpolator())
                .setDuration(parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .withEndAction {
                    originalChildren.forEach { child -> parent.removeView(child) }

                    newView.translationZ = 0f

                    if (shouldClearBackground) {
                        newView.background = null
                    }

                    callback.onComplete(newViewResult)
                }
        }
    }

    private fun View.applyWindowBackground(): Boolean {
        if (background != null) return false

        val value = TypedValue().also {
            context.theme.resolveAttribute(android.R.attr.windowBackground, it, true)
        }

        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            setBackgroundColor(value.data)
        } else {
            background = context.getDrawable(value.data)
        }

        return true
    }
}