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

import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.nhaarman.acorn.android.R
import com.nhaarman.acorn.android.internal.applyWindowBackground
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewControllerFactory
import com.nhaarman.acorn.android.transition.internal.doOnPreDraw
import com.nhaarman.acorn.presentation.Scene

/**
 * A [Transition] that fades the current [View] out to bottom, revealing the new
 * View underneath.
 *
 * This class assumes there is currently a single View present in the parent
 * [ViewGroup]. If there is more than one View, the first child is used to fade
 * out.
 *
 * Any views that live in the parent [ViewGroup] before the transition starts
 * wil be removed.
 */
class FadeOutToBottomTransition(
    private val viewController: (ViewGroup) -> ViewController
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        // We're assuming a single View is present.
        val originalChildren = (0..parent.childCount).map { parent.getChildAt(it) }
        val originalView = originalChildren.firstOrNull()

        val newViewController = viewController(parent)
        val newView = newViewController.view

        if (originalView == null) {
            parent.addView(newView)
            callback.onComplete(newViewController)
            return
        }

        parent.addView(newView, 0)
        originalView.translationZ = parent.resources.getDimension(R.dimen.acorn_fadeinfrombottomtransition_translationz)
        if (originalView.background == null) originalView.applyWindowBackground()

        callback.attach(newViewController)

        parent.doOnPreDraw {
            originalView.animate()
                .translationYBy(parent.measuredHeight / 5f)
                .alpha(0f)
                .setInterpolator(FastOutLinearInInterpolator())
                .setDuration(parent.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .withEndAction {
                    originalChildren.forEach { child -> parent.removeView(child) }

                    callback.onComplete(newViewController)
                }
        }
    }

    companion object {

        fun from(viewControllerFactory: ViewControllerFactory): (Scene<*>) -> FadeOutToBottomTransition = { scene ->
            FadeOutToBottomTransition { parent ->
                viewControllerFactory.viewControllerFor(scene, parent)
            }
        }
    }
}