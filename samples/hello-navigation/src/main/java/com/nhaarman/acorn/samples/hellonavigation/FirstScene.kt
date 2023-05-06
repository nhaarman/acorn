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

package com.nhaarman.acorn.samples.hellonavigation

import android.view.View
import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.presentation.ViewProvidingScene
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.samples.hellonavigation.databinding.FirstSceneBinding

/**
 * A Scene that shows a button to navigate to the second Scene.
 *
 * This Scene exposes the [FirstScene.Events] callback interface to let the
 * [Navigator] know the second Scene is requested. The Navigator implements this
 * interface to decide whether to make a transition.
 *
 * This Scene does not handle any state restoration, since there is no state
 * worth saving.
 * In cases where state _is_ worth saving, your Scene should generally implement
 * [SavableScene].
 */
class FirstScene(
    /** The listener callback to be notified when an event happens. */
    private val listener: Events,
) : ViewProvidingScene<FirstSceneContainer> {

    override fun createViewController(parent: ViewGroup): ViewController {
        return FirstSceneViewController(parent.inflate(R.layout.first_scene))
    }

    override fun attach(v: FirstSceneContainer) {
        v.onSecondSceneClicked { listener.secondSceneRequested() }
    }

    /**
     * An interface to let users of this Scene know something happened.
     */
    interface Events {

        fun secondSceneRequested()
    }
}

interface FirstSceneContainer : Container {

    fun onSecondSceneClicked(f: () -> Unit)
}

class FirstSceneViewController(
    override val view: View,
) : RestorableViewController, FirstSceneContainer {

    private val binding = FirstSceneBinding.bind(view)

    override fun onSecondSceneClicked(f: () -> Unit) {
        binding.secondSceneButton.setOnClickListener { f() }
    }
}
