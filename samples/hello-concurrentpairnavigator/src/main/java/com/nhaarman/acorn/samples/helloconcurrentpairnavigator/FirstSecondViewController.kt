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
import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.RestorableViewController
import com.nhaarman.acorn.android.util.inflateView
import com.nhaarman.acorn.navigation.experimental.CombinedContainer
import com.nhaarman.acorn.navigation.experimental.ExperimentalConcurrentPairNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.state.ContainerState
import kotlinx.android.synthetic.main.first_and_second_scene.view.*

@UseExperimental(ExperimentalConcurrentPairNavigator::class)
class FirstSecondViewController(
    private val parent: ViewGroup
) : RestorableViewController, CombinedContainer {

    override val view: View by lazy {
        parent.inflateView(R.layout.first_and_second_scene)
    }

    override val firstContainer: Container
        get() = FirstSceneViewController(view.firstSceneRoot)

    override val secondContainer: Container
        get() = SecondSceneViewController(view.secondSceneRoot)

    override fun saveInstanceState(): ContainerState {
        return ContainerState()
    }

    override fun restoreInstanceState(bundle: ContainerState) {
    }
}
