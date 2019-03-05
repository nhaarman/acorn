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

package com.nhaarman.acorn.navigation.experimental

import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.RestorableContainer
import com.nhaarman.acorn.presentation.SavableContainer
import com.nhaarman.acorn.state.ContainerState
import com.nhaarman.acorn.state.get

/**
 * A [Container] that combines two Containers into one.
 *
 * This interface is used in conjunction with [ConcurrentPairNavigator].
 */
@ExperimentalConcurrentPairNavigator
interface CombinedContainer : RestorableContainer {

    val firstContainer: Container
    val secondContainer: Container

    override fun saveInstanceState(): ContainerState {
        return ContainerState().also {
            it["first"] = (firstContainer as? SavableContainer)?.saveInstanceState()
            it["second"] = (secondContainer as? SavableContainer)?.saveInstanceState()
        }
    }

    override fun restoreInstanceState(bundle: ContainerState) {
        bundle.get<ContainerState>("first")?.let {
            (firstContainer as? RestorableContainer)?.restoreInstanceState(it)
        }

        bundle.get<ContainerState>("second")?.let {
            (secondContainer as? RestorableContainer)?.restoreInstanceState(it)
        }
    }
}
