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
 * A [SceneTransition] that fades the current [View] out to bottom, revealing the new
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
) : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
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
