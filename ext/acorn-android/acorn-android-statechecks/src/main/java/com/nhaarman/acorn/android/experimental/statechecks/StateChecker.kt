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

import acorn.logger
import android.util.Log.d
import com.nhaarman.acorn.android.navigation.NavigatorProvider
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.SavableNavigator
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState

@ExperimentalStateChecker
internal class StateChecker {

    private var disposable: DisposableHandle? = null
        set(value) {
            field?.dispose()
            field = value
        }

    fun start(navigatorProvider: NavigatorProvider, instance: SavableNavigator) {
        disposable = instance
            .addNavigatorEventsListener(object : NavigatorEventsAdapter() {
                override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                    checkState(navigatorProvider)
                }
            })
    }

    private fun checkState(navigatorProvider: NavigatorProvider) {
        logger?.w("StateChecker", "==== Checking state restoration ====")

        val state = trySavingState(navigatorProvider)
        tryRestoring(state, navigatorProvider)

        logger?.w("StateChecker", "==== State restoration OK ==========")
    }

    private fun trySavingState(navigatorProvider: NavigatorProvider): NavigatorState? {
        try {
            d("StateChecker", "Saving navigator state.")
            val state = navigatorProvider.saveNavigatorState()
            d("StateChecker", "Saving navigator state successful.")
            return state
        } catch (t: Throwable) {
            throw StateSavingFailedException(cause = t)
        }
    }

    private fun tryRestoring(state: NavigatorState?, navigatorProvider: NavigatorProvider) {
        try {
            d("StateChecker", "Instantiating Navigator using state:")
            d("StateChecker", "\t\t$state.")
            val newNavigator = navigatorProvider.newInstance(state)

            d("StateChecker", "Destroying Navigator.")
            val listener = newNavigator.addNavigatorEventsListener(NavigatorEventsAdapter())
            newNavigator.onDestroy()
            listener.dispose()
        } catch (t: Throwable) {
            throw StateRestorationFailedException(
                state = state,
                cause = t
            )
        }
    }

    fun stop() {
        disposable = null
    }
}
