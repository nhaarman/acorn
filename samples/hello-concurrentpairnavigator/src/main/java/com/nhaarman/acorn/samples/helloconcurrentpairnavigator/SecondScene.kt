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

import android.view.View
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.samples.helloconcurrentpairnavigator.databinding.SecondSceneBinding

class SecondScene(
    private val listener: Events,
) : Scene<SecondSceneContainer> {

    override fun attach(v: SecondSceneContainer) {
        v.onBackClicked { listener.onBackClicked() }
    }

    interface Events {

        fun onBackClicked()
    }
}

interface SecondSceneContainer : Container {

    fun onBackClicked(f: () -> Unit)
}

class SecondSceneViewController(
    override val view: View,
) : RestorableViewController, SecondSceneContainer {

    private val binding = SecondSceneBinding.bind(view)

    override fun onBackClicked(f: () -> Unit) {
        binding.firstSceneButton.setOnClickListener { f() }
    }
}
