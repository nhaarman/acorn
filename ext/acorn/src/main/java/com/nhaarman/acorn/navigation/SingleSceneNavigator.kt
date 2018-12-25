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
        execute(state.finish())
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
                return listener !in state.listeners
            }

            override fun dispose() {
                state.removeListener(listener)
            }
        }
    }

    @CallSuper
    override fun onStart() {
        v("SingleSceneNavigator", "onStart")
        execute(state.start())
    }

    @CallSuper
    override fun onStop() {
        v("SingleSceneNavigator", "onStop")
        execute(state.stop())
    }

    @CallSuper
    override fun onDestroy() {
        v("SingleSceneNavigator", "onDestroy")
        execute(state.destroy())
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is LifecycleState.Destroyed) return false

        v("SingleSceneNavigator", "onBackPressed")
        execute(state.finish())

        return true
    }

    private fun execute(transition: StateTransition) {
        state = transition.newState
        transition.action?.invoke()
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

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition

        abstract fun finish(): StateTransition

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

            override fun start(): StateTransition {
                return StateTransition(Active(scene, listeners)) {
                    scene.onStart()
                    listeners.forEach { it.scene(scene, null) }
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    scene.onDestroy()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    listeners.forEach { it.finished() }
                    scene.onDestroy()
                }
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

            override fun start(): StateTransition {
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(Inactive(scene, listeners)) {
                    scene.onStop()
                }
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    scene.onStop()
                    scene.onDestroy()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    listeners.forEach { it.finished() }
                    scene.onStop()
                    scene.onDestroy()
                }
            }
        }

        class Destroyed : LifecycleState() {

            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): StateTransition {
                w("LifecycleState", "Warning: Cannot start state after it is destroyed.")
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                return StateTransition(this)
            }
        }
    }

    private class StateTransition(
        val newState: LifecycleState,
        val action: (() -> Unit)? = null
    )

    companion object {

        private var NavigatorState.sceneState: SceneState?
            get() = get("scene:state")
            set(value) {
                set("scene:state", value)
            }
    }
}