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
import androidx.core.view.doOnPreDraw
import com.nhaarman.acorn.android.transition.Transition
import com.nhaarman.acorn.android.util.inflateView
import com.nhaarman.acorn.navigation.experimental.ExperimentalConcurrentPairNavigator
import kotlinx.android.synthetic.main.second_scene.view.*

@UseExperimental(ExperimentalConcurrentPairNavigator::class)
object FirstSecondTransition : Transition {

    override fun execute(parent: ViewGroup, callback: Transition.Callback) {
        val secondScene = parent.inflateView(R.layout.second_scene)
        parent.addView(secondScene)

        val viewController = FirstSecondViewController(parent)
        callback.attach(viewController)

        parent.doOnPreDraw {
            secondScene.overlayView
                .apply {
                    alpha = 0f
                    animate().alpha(1f)
                }

            secondScene.cardView.apply {
                translationY = height.toFloat()
                animate().translationY(0f)
                    .withEndAction {
                        callback.onComplete(viewController)
                    }
            }
        }
    }
}
