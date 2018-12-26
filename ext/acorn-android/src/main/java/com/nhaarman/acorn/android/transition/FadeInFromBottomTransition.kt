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

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.R
import com.nhaarman.acorn.android.internal.applyWindowBackground
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.transition.internal.doOnPreDraw

/**
 * A transition that fades the new [View] from the bottom.
 *
 * Any views that live in the parent [ViewGroup] before the transition starts
 * wil be removed.
 */
class FadeInFromBottomTransition(
    private val viewController: (ViewGroup) -> ViewController
) : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val originalChildren = (0..parent.childCount).map { parent.getChildAt(it) }

        val newViewResult = viewController(parent)

        val newView = newViewResult.view
        parent.addView(newView)

        val shouldClearBackground = newView.background == null
        if (newView.background == null) newView.applyWindowBackground()

        newView.apply {
            translationY = parent.height / 5f
            translationZ = parent.resources.getDimension(R.dimen.acorn_fadeinfrombottomtransition_translationz)
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