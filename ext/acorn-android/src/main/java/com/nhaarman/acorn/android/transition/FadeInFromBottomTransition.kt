/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.transition

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup
import com.nhaarman.bravo.android.R
import com.nhaarman.bravo.android.internal.applyWindowBackground
import com.nhaarman.bravo.android.presentation.ViewController
import com.nhaarman.bravo.android.transition.internal.doOnPreDraw

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