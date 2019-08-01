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

package com.nhaarman.acorn.samples.hellostaterestoration

import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.StackNavigator
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import kotlin.reflect.KClass

/**
 * A [Navigator] implementation that can be saved and restored.
 *
 * This implementation extends the [StackNavigator] which implements default
 * behavior for saving the state of this Navigator and its Scene.
 */
class HelloStateRestorationNavigator private constructor(
    private var counter: Int,
    savedState: NavigatorState?
) : StackNavigator(savedState),
    HelloStateRestorationScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(HelloStateRestorationScene.create(0, this))
    }

    override fun nextRequested() {
        counter++
        push(HelloStateRestorationScene.create(counter, this))
    }

    override fun onBackPressed(): Boolean {
        counter--
        return super.onBackPressed()
    }

    /**
     * Instantiates a [Scene] instance for given [sceneClass] and [state].
     *
     * This function is called when restoring the Navigator from a saved state.
     */
    override fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            HelloStateRestorationScene::class -> HelloStateRestorationScene.create(state!!, this)
            else -> error("Unknown Scene class: $sceneClass")
        }
    }

    /**
     * Overrides the default instance state saving behavior to add the counter
     * value.
     */
    override fun saveInstanceState(): NavigatorState {
        return super.saveInstanceState()
            .also { it.counter = counter }
    }

    companion object {

        /**
         * Creates a new Navigator instance for given optional saved state.
         */
        fun create(savedState: NavigatorState?): HelloStateRestorationNavigator {
            val counter = savedState?.counter ?: 0
            return HelloStateRestorationNavigator(counter, savedState)
        }

        private var NavigatorState.counter: Int?
            get() = get("counter")
            set(value) {
                this["counter"] = value
            }
    }
}
