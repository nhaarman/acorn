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

package com.nhaarman.acorn.android.experimental

import com.nhaarman.acorn.android.dispatching.SceneDispatcher
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator

/**
 * A [SceneDispatcher] that wraps an existing instance, notifying the
 * experimental [AcornEvents] class of dispatching events.
 */
@OptIn(ExperimentalAcornEvents::class)
class HookingSceneDispatcher private constructor(
    private val delegate: SceneDispatcher,
    private val navigatorProvider: NavigatorProvider,
) : SceneDispatcher by delegate {

    override fun dispatchScenesFor(navigator: Navigator): DisposableHandle {
        AcornEvents.onStartDispatching(navigatorProvider, navigator)
        val original = delegate.dispatchScenesFor(navigator)

        return object : DisposableHandle {

            override fun dispose() {
                AcornEvents.onStopDispatching(navigatorProvider, navigator)
                original.dispose()
            }

            override fun isDisposed(): Boolean {
                return original.isDisposed()
            }
        }
    }

    companion object {

        fun create(
            delegate: SceneDispatcher,
            navigatorProvider: NavigatorProvider,
        ): HookingSceneDispatcher {
            return HookingSceneDispatcher(
                delegate,
                navigatorProvider,
            )
        }
    }
}
