package com.nhaarman.bravo.android.transition

import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import com.nhaarman.bravo.android.R
import com.nhaarman.bravo.presentation.Scene

class FadeOutToBottomTransition(
    private val view: (ViewGroup) -> ViewResult
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        // We're assuming a single View is present.
        val originalChildren = parent.children.asIterable().toList()
        val originalView = originalChildren.firstOrNull()

        val newViewResult = view(parent)
        val newView = newViewResult.view

        if (originalView == null) {
            parent.addView(newView)
            callback.onComplete(newViewResult)
            return
        }

        parent.addView(newView, 0)
        originalView.translationZ = parent.resources.getDimension(R.dimen.bravo_fadeinfrombottomtransition_translationz)
        originalView.applyWindowBackground()

        callback.attach(newViewResult)

        parent.doOnPreDraw {
            originalView.animate()
                .translationYBy(parent.measuredHeight / 5f)
                .alpha(0f)
                .setInterpolator(FastOutLinearInInterpolator())
                .setDuration(parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .withEndAction {
                    originalChildren.forEach { child -> parent.removeView(child) }

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

    companion object {

        fun from(viewFactory: ViewFactory): (Scene<*>) -> FadeOutToBottomTransition = { scene ->
            FadeOutToBottomTransition { parent -> viewFactory.viewFor(scene.key, parent) }
        }

        fun from(viewFactory: ViewFactory, scene: Scene<*>): FadeOutToBottomTransition {
            return FadeOutToBottomTransition { parent -> viewFactory.viewFor(scene.key, parent) }
        }
    }
}