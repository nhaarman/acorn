/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.internal.v
import com.nhaarman.bravo.internal.w
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.get
import com.nhaarman.bravo.state.navigatorState
import com.nhaarman.bravo.util.lazyVar

/**
 * A simple [Navigator] that only hosts a single [Scene].
 *
 * This Navigator implements [SaveableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by [saveInstanceState].
 */
abstract class SingleSceneNavigator<Events : Navigator.Events>(
    private val savedState: NavigatorState?
) : Navigator<Events>, SaveableNavigator, OnBackPressListener {

    /**
     * Creates the [Scene] instance to host.
     *
     * @param state An optional saved state instance to restore the Scene's
     *              state from.
     */
    abstract fun createScene(state: SceneState?): Scene<out Container>

    private val scene by lazy { createScene(savedState?.sceneState) }

    private var state by lazyVar { LifecycleState.create(scene) }

    protected val listeners = mutableListOf<Events>()
    override fun addListener(listener: Events): DisposableHandle {
        listeners += listener

        if (state is LifecycleState.Active) {
            listener.scene(scene, null)
        }

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in listeners
            }

            override fun dispose() {
                listeners -= listener
            }
        }
    }

    override fun onStart() {
        v("SingleSceneNavigator", "onStart")

        state = state.start()
        listeners.forEach { it.scene(scene, null) }
    }

    override fun onStop() {
        v("SingleSceneNavigator", "onStop")
        state = state.stop()
    }

    override fun onDestroy() {
        v("SingleSceneNavigator", "onDestroy")
        state = state.destroy()
    }

    override fun onBackPressed(): Boolean {
        v("SingleSceneNavigator", "onBackPressed")
        state = state.stop().destroy()

        listeners.forEach { it.finished() }
        return true
    }

    override fun saveInstanceState(): NavigatorState {
        return navigatorState {
            it.sceneState = (scene as? SaveableScene)?.saveInstanceState()
        }
    }

    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract fun start(): LifecycleState
        abstract fun stop(): LifecycleState
        abstract fun destroy(): LifecycleState

        companion object {

            fun create(scene: Scene<out Container>): LifecycleState {
                return Inactive(scene)
            }
        }

        class Inactive(val scene: Scene<out Container>) : LifecycleState() {

            override fun start(): LifecycleState {
                scene.onStart()
                return Active(scene)
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                scene.onDestroy()
                return Destroyed()
            }
        }

        class Active(val scene: Scene<out Container>) : LifecycleState() {

            override fun start(): LifecycleState {
                return this
            }

            override fun stop(): LifecycleState {
                scene.onStop()
                return Inactive(scene)
            }

            override fun destroy(): LifecycleState {
                scene.onStop()
                scene.onDestroy()
                return Destroyed()
            }
        }

        class Destroyed : LifecycleState() {

            override fun start(): LifecycleState {
                w("LifecycleState", "Warning: Cannot start state after it is destroyed.")
                return this
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                return this
            }
        }
    }

    companion object {

        private var NavigatorState.sceneState: SceneState?
            get() = get("scene:state")
            set(value) {
                set("scene:state", value)
            }
    }
}