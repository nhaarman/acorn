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

package com.nhaarman.acorn.samples.hellotransitionanimation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.nhaarman.acorn.android.transition.SceneTransition

object FirstToSecondTransition : SceneTransition {

    override fun execute(parent: ViewGroup, callback: SceneTransition.Callback) {
        val currentLayout = parent.getChildAt(0)

        val newLayout = LayoutInflater.from(parent.context).inflate(R.layout.second_scene, parent, false)
        parent.addView(newLayout)

        parent.doOnPreDraw {
            newLayout.translationX = newLayout.width.toFloat()

            currentLayout
                .animate()
                .translationX((-currentLayout.width).toFloat())
                .withEndAction { parent.removeView(currentLayout) }

            newLayout
                .animate()
                .translationX(0f)
                .withEndAction {
                    callback.onComplete(SecondSceneViewController(newLayout))
                }
        }
    }
}
