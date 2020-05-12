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

package com.nhaarman.acorn.samples.hellostartactivity

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewProvidingScene
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.Container
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.first_scene.*

class FirstScene(
    private val listener: Events
) : ViewProvidingScene<FirstSceneContainer> {

    override fun createViewController(parent: ViewGroup): ViewController {
        return FirstSceneViewController(parent.inflate(R.layout.first_scene))
    }

    override fun attach(v: FirstSceneContainer) {
        v.onButtonClicked { listener.settingsRequested() }
    }

    interface Events {

        fun settingsRequested()
    }
}

interface FirstSceneContainer : Container {

    fun onButtonClicked(f: () -> Unit)
}

class FirstSceneViewController(
    override val view: View
) : RestorableViewController, FirstSceneContainer, LayoutContainer {

    override fun onButtonClicked(f: () -> Unit) {
        settingsButton.setOnClickListener { f() }
    }

    override val containerView = view
}
