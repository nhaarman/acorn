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
import com.nhaarman.acorn.android.transition.Transition
import kotlinx.android.synthetic.main.first_and_second_scene.view.*
import kotlinx.android.synthetic.main.second_scene.view.*

object SecondFirstTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val firstAndSecondRoot = parent.findViewById<ViewGroup>(R.id.firstAndSecondRoot)
        if (firstAndSecondRoot != null) {
            val firstRoot = firstAndSecondRoot.firstSceneRoot
            val secondRoot = firstAndSecondRoot.secondSceneRoot

            firstAndSecondRoot.removeView(firstRoot)
            parent.addView(firstRoot, 0)

            val viewController = FirstSceneViewController(parent)
            callback.attach(viewController)

            secondRoot.overlayView
                .animate()
                .alpha(0f)

            secondRoot.cardView
                .animate()
                .translationY(secondRoot.cardView.height.toFloat())
                .withEndAction {
                    parent.removeView(firstAndSecondRoot)
                    callback.onComplete(viewController)
                }

            return
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
}
