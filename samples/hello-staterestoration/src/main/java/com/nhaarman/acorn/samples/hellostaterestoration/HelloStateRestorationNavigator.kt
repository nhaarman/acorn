/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
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