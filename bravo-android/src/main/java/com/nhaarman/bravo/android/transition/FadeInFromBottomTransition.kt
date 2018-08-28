package com.nhaarman.bravo.android.transition

import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import com.nhaarman.bravo.android.R
import com.nhaarman.bravo.android.internal.applyWindowBackground

class FadeInFromBottomTransition(
    private val view: (ViewGroup) -> ViewResult
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val originalChildren = parent.children.asIterable().toList()

        val newViewResult = view(parent)

        val newView = newViewResult.view
        parent.addView(newView)

        val shouldClearBackground = newView.background == null
        if (newView.background == null) newView.applyWindowBackground()

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
}