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

package com.nhaarman.acorn.navigation

import androidx.annotation.CallSuper
import com.nhaarman.acorn.OnBackPressListener
import com.nhaarman.acorn.internal.v
import com.nhaarman.acorn.internal.w
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.navigatorState
import com.nhaarman.acorn.util.lazyVar

/**
 * A simple [Navigator] that only hosts a single [Scene].
 *
 * This Navigator implements [SavableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by [saveInstanceState].
 */
abstract class SingleSceneNavigator(
    private val savedState: NavigatorState?
) : Navigator, SavableNavigator, OnBackPressListener {

    /**
     * Creates the [Scene] instance to host.
     *
     * @param state An optional saved state instance to restore the Scene's
     *              state from.
     */
    protected abstract fun createScene(state: SceneState?): Scene<out Container>

    /**
     * Finishes this Navigator.
     *
     * If this Navigator is currently active, the current Scene will go through
     * its destroying lifecycle calling [Scene.onStop] and [Scene.onDestroy].
     *
     * If this Navigator is currently not active, the current Scene will only
     * have its [Scene.onDestroy] method called.
     *
     * Calling this method when the Navigator has been destroyed will have no
     * effect.
     */
    fun finish() {
        state = state.finish()
    }

    private val scene by lazy { createScene(savedState?.sceneState) }

    private var state by lazyVar { LifecycleState.create(scene) }

    @CallSuper
    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        state.addListener(listener)

        if (state is LifecycleState.Active) {
            listener.scene(scene, null)
        }

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in state.listeners
            }

            override fun dispose() {
                state.removeListener(listener)
            }
        }
    }

    @CallSuper
    override fun onStart() {
        v("SingleSceneNavigator", "onStart")

        state = state.start()
    }

    @CallSuper
    override fun onStop() {
        v("SingleSceneNavigator", "onStop")
        state = state.stop()
    }

    @CallSuper
    override fun onDestroy() {
        v("SingleSceneNavigator", "onDestroy")
        state = state.destroy()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is LifecycleState.Destroyed) return false

        v("SingleSceneNavigator", "onBackPressed")
        state = state.finish()

        return true
    }

    @CallSuper
    override fun saveInstanceState(): NavigatorState {
        return navigatorState {
            it.sceneState = (scene as? SavableScene)?.saveInstanceState()
        }
    }

    @CallSuper
    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): LifecycleState
        abstract fun stop(): LifecycleState
        abstract fun destroy(): LifecycleState

        abstract fun finish(): LifecycleState

        companion object {

            fun create(scene: Scene<out Container>): LifecycleState {
                return Inactive(scene, emptyList())
            }
        }

        class Inactive(
            private val scene: Scene<out Container>,
            override var listeners: List<Navigator.Events>
        ) : LifecycleState() {

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): LifecycleState {
                scene.onStart()
                listeners.forEach { it.scene(scene, null) }
                return Active(scene, listeners)
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                scene.onDestroy()
                return Destroyed()
            }

            override fun finish(): LifecycleState {
                listeners.forEach { it.finished() }
                return destroy()
            }
        }

        class Active(
            private val scene: Scene<out Container>,
            override var listeners: List<Navigator.Events>
        ) : LifecycleState() {

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): LifecycleState {
                return this
            }

            override fun stop(): LifecycleState {
                scene.onStop()
                return Inactive(scene, listeners)
            }

            override fun destroy(): LifecycleState {
                scene.onStop()
                scene.onDestroy()
                return Destroyed()
            }

            override fun finish(): LifecycleState {
                listeners.forEach { it.finished() }
                return destroy()
            }
        }

        class Destroyed : LifecycleState() {

            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
            }

            override fun removeListener(listener: Navigator.Events) {
            }

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

            override fun finish(): LifecycleState {
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