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

import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator

/**
 * An experimental class to provide hooks for Acorn.
 */
@ExperimentalAcornEvents
object AcornEvents {

    private var dispatchingListeners = listOf<DispatchingListener>()

    fun registerDispatchingListener(listener: DispatchingListener): DisposableHandle {
        dispatchingListeners += listener
        return object : DisposableHandle {

            override fun dispose() {
                dispatchingListeners -= listener
            }

            override fun isDisposed(): Boolean {
                return listener in dispatchingListeners
            }
        }
    }

    fun onStartDispatching(navigatorProvider: NavigatorProvider, instance: Navigator) {
        dispatchingListeners.forEach { it.onStartDispatching(navigatorProvider, instance) }
    }

    fun onStopDispatching(navigatorProvider: NavigatorProvider, instance: Navigator) {
        dispatchingListeners.forEach { it.onStopDispatching(navigatorProvider, instance) }
    }

    interface DispatchingListener {

        /**
         * Called when a component starts dispatching Scenes for given [instance].
         *
         * @param navigatorProvider The [NavigatorProvider] that provides the
         * Navigator [instance].
         * @param instance The [Navigator] instance that provides the Scenes to
         * be dispatched.
         */
        fun onStartDispatching(navigatorProvider: NavigatorProvider, instance: Navigator)

        /**
         * Called when the component that started dispatching Scenes for given
         * [instance] stops dispatching.
         *
         * @param navigatorProvider The [NavigatorProvider] that provides the
         * Navigator [instance].
         * @param instance The [Navigator] instance that provided the Scenes to
         * be dispatched.
         */
        fun onStopDispatching(navigatorProvider: NavigatorProvider, instance: Navigator)
    }
}
