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

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewProvidingScene
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.Container
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.second_scene.*

class SecondScene(
    private val listener: Events
) : ViewProvidingScene<SecondSceneContainer> {

    override fun createViewController(parent: ViewGroup): ViewController {
        return SecondSceneViewController(parent.inflate(R.layout.second_scene))
    }

    override fun attach(v: SecondSceneContainer) {
        v.onFirstSceneClicked { listener.onFirstSceneRequested() }
    }

    interface Events {

        fun onFirstSceneRequested()
    }
}

interface SecondSceneContainer : Container {

    fun onFirstSceneClicked(f: () -> Unit)
}

class SecondSceneViewController(
    override val view: View
) : RestorableViewController, SecondSceneContainer, LayoutContainer {

    override fun onFirstSceneClicked(f: () -> Unit) {
        firstSceneButton.setOnClickListener { f() }
    }

    override val containerView = view
}
