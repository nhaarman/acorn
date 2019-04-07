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

package com.nhaarman.acorn.android.experimental.statechecks

import com.nhaarman.acorn.android.experimental.AcornEvents
import com.nhaarman.acorn.android.experimental.ExperimentalAcornEvents
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.SavableNavigator

@ExperimentalStateChecker
@UseExperimental(ExperimentalAcornEvents::class)
internal class StateCheckerHook {

    private val listener = MyDispatchingListener()

    fun start() {
        AcornEvents.registerDispatchingListener(listener)
    }

    private class MyDispatchingListener : AcornEvents.DispatchingListener {

        private val checkers = mutableMapOf<NavigatorProvider, StateChecker>()

        override fun onStartDispatching(navigatorProvider: NavigatorProvider, instance: Navigator) {
            if (instance is SavableNavigator) {
                checkers.getOrPut(navigatorProvider) { StateChecker() }
                    .start(navigatorProvider, instance)
            }
        }

        override fun onStopDispatching(navigatorProvider: NavigatorProvider, instance: Navigator) {
            checkers[navigatorProvider]?.stop()
        }
    }
}
