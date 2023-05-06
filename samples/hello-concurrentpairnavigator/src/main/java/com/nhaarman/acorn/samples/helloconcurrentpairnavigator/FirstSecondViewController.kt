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
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.navigation.experimental.CombinedContainer
import com.nhaarman.acorn.navigation.experimental.ExperimentalConcurrentPairNavigator
import com.nhaarman.acorn.presentation.Container

@OptIn(ExperimentalConcurrentPairNavigator::class)
class FirstSecondViewController(
    override val view: ViewGroup,
) : ViewController, CombinedContainer {

    override val firstContainer: Container by lazy {
        FirstSceneViewController(view.findViewById(R.id.firstSceneRoot))
    }

    override val secondContainer: Container by lazy {
        SecondSceneViewController(view.findViewById(R.id.secondSceneRoot))
    }
}
