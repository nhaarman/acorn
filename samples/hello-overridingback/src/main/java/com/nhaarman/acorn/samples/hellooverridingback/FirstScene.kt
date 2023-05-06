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

package com.nhaarman.acorn.samples.hellooverridingback

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.OnBackPressListener
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewProvidingScene
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.samples.hellooverridingback.databinding.FirstSceneBinding
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class FirstScene : ViewProvidingScene<FirstSceneContainer> {

    override fun createViewController(parent: ViewGroup): ViewController {
        return FirstSceneViewController(parent.inflate(R.layout.first_scene))
    }
}

interface FirstSceneContainer : Container {

    fun setBackPressListener(f: () -> Unit)
}

class FirstSceneViewController(
    override val view: View,
) : RestorableViewController,
    FirstSceneContainer,
    OnBackPressListener {

    private val binding = FirstSceneBinding.bind(view)

    private var backPressListener: (() -> Unit)? = null

    override fun setBackPressListener(f: () -> Unit) {
        this.backPressListener = f
    }

    override fun onBackPressed(): Boolean {
        binding.konfettiView.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(12))
            .setPosition(-50f, binding.konfettiView.width + 50f, -50f, -50f)
            .streamFor(300, 1000L)

        return true
    }
}
