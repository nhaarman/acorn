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

package com.nhaarman.acorn.samples.helloconcurrentpairnavigator

import android.view.ViewGroup
import com.nhaarman.acorn.android.transition.SceneTransition
import kotlinx.android.synthetic.main.first_and_second_scene.view.*
import kotlinx.android.synthetic.main.first_scene.view.*
import kotlinx.android.synthetic.main.second_scene.view.*

object SecondFirstTransition : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        val firstAndSecondRoot = parent.findViewById<ViewGroup>(R.id.firstAndSecondRoot)

        if (firstAndSecondRoot != null) {
            normalizeLayout(parent)
        }

        val viewController = FirstSceneViewController(parent)
        callback.attach(viewController)

        val overlayView = parent.secondSceneRoot.overlayView
        val cardView = parent.secondSceneRoot.cardView

        overlayView.animate()
            .alpha(0f)

        cardView.animate()
            .translationY(cardView.height.toFloat())
            .withEndAction {
                parent.removeView(parent.secondSceneRoot)
                callback.onComplete(viewController)
            }
    }

    /**
     * 'Normalizes' the layout by removing the intermediate FrameLayout.
     * The resulting layout in [parent] contains the exact layout as it would
     * be when transitioning using [FirstSecondTransition].
     */
    private fun normalizeLayout(parent: ViewGroup) {
        val firstAndSecondRoot = parent.firstAndSecondRoot

        firstAndSecondRoot.firstSceneRoot.let {
            firstAndSecondRoot.removeView(it)
            parent.addView(it)
        }

        firstAndSecondRoot.secondSceneRoot.let {
            firstAndSecondRoot.removeView(it)
            parent.addView(it)
        }

        parent.removeView(firstAndSecondRoot)
    }
}
