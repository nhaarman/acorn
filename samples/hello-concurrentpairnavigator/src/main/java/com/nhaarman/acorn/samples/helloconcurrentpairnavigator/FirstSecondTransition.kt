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
import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.util.inflateView
import com.nhaarman.acorn.samples.helloconcurrentpairnavigator.databinding.SecondSceneBinding

object FirstSecondTransition : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        val secondScene = parent.inflateView(R.layout.second_scene)
        val secondSceneBinding = SecondSceneBinding.bind(secondScene)
        parent.addView(secondScene)

        val viewController = FirstSecondViewController(parent)
        callback.attach(viewController)

        parent.doOnPreDraw {
            secondSceneBinding.overlayView
                .apply {
                    alpha = 0f
                    animate().alpha(1f)
                }

            secondSceneBinding.cardView.apply {
                translationY = height.toFloat()
                animate().translationY(0f)
                    .withEndAction {
                        callback.onComplete(viewController)
                    }
            }
        }
    }
}
