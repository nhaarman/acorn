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
import kotlin.reflect.KClass

/**
 * A navigator class that can switch between [Scene]s, but has no 'back'
 * behavior.
 *
 * This Navigator is able to save and restore its instance state in
 * [saveInstanceState], but does not implement [SavableNavigator] itself.
 * You can opt in to this state saving by explicitly implementing the
 * [SavableNavigator] interface.
 *
 * @param savedState An optional instance that contains saved state as returned
 * by [saveInstanceState].
 */
abstract class ReplacingNavigator(
    private val savedState: NavigatorState?
) : Navigator, OnBackPressListener {

    /**
     * Returns the Scene this Navigator should start with.
     *
     * Will only be called once in the lifetime of the Navigator, and zero times
     * if the Navigator is being restored from a saved state.
     */
    protected abstract fun initialScene(): Scene<out Container>

    /**
     * Instantiates the Scene for given [sceneClass] and [state].
     *
     * This method is usually invoked when the Navigator is being restored from
     * a saved state.
     *
     * @param sceneClass The class of the [Scene] to instantiate
     * @param state An optional saved state instance to restore the new Scene's
     * state from.
     */
    protected abstract fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container>

    /**
     * Replaces the current Scene with [newScene].
     *
     * If this Navigator is currently active, the current Scene will go through
     * its destroying lifecycle calling [Scene.onStop] and [Scene.onDestroy].
     * [newScene] will have its [Scene.onStart] method called, and any listeners
     * will be notified of the Scene change.
     *
     * If this Navigator is currently stopped, no Scene lifecycle events will be
     * called at all, and listeners will not be notified. Calling this Navigator's
     * [onStart] will trigger a call to the [Scene.onStart] method of [newScene]
     * notify the listeners.
     *
     * Calling this method when the Navigator has been destroyed will have no
     * effect.
     *
     * @param newScene The [Scene] instance that should replace the current one.
     * @param data Any transition data for this transition.
     */
    fun replace(newScene: Scene<out Container>, data: TransitionData? = null) {
        execute(state.replaceWith(newScene, data))
    }

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

    private var state by lazyVar {
        fun initialScene(): Scene<out Container> {
            val savedClass = savedState?.sceneClass ?: return this@ReplacingNavigator.initialScene()
            val savedState = savedState.sceneState

            return instantiateScene(savedClass, savedState)
        }

        LifecycleState.create(initialScene())
    }

    @CallSuper
    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        state.addListener(listener)

        if (state is LifecycleState.Active) {
            listener.scene(state.scene, null)
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
        v("ReplacingNavigator", "onStart")

        execute(state.start())
    }

    @CallSuper
    override fun onStop() {
        v("ReplacingNavigator", "onStop")
        execute(state.stop())
    }

    @CallSuper
    override fun onDestroy() {
        v("ReplacingNavigator", "onDestroy")
        execute(state.destroy())
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is LifecycleState.Destroyed) return false

        v("ReplacingNavigator", "onBackPressed")
        execute(state.finish())

        return true
    }

    private fun execute(transition: StateTransition) {
        state = transition.newState
        transition.action?.invoke()
    }

    @CallSuper
    open fun saveInstanceState(): NavigatorState {
        val scene = state.scene
        if (scene !is SavableScene) return NavigatorState()

        return navigatorState {
            it.sceneClass = scene::class
            it.sceneState = scene.saveInstanceState()
        }
    }

    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract val scene: Scene<out Container>
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition

        abstract fun replaceWith(scene: Scene<out Container>, data: TransitionData?): StateTransition
        abstract fun finish(): StateTransition

        companion object {

            fun create(scene: Scene<out Container>): LifecycleState {
                return Inactive(scene, emptyList())
            }
        }

        class Inactive(
            override val scene: Scene<out Container>,
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
                    listeners.forEach { it.scene(scene) }
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed(scene)) {
                    scene.onDestroy()
                }
            }

            override fun replaceWith(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                return StateTransition(Inactive(scene, listeners))
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed(scene)) {
                    listeners.forEach { it.finished() }
                    scene.onDestroy()
                }
            }
        }

        class Active(
            override val scene: Scene<out Container>,
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
                return StateTransition(Destroyed(scene)) {
                    scene.onStop()
                    scene.onDestroy()
                }
            }

            override fun replaceWith(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                return StateTransition(Active(scene, listeners)) {
                    this.scene.onStop()
                    this.scene.onDestroy()
                    listeners.forEach { it.scene(scene, data) }
                    scene.onStart()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed(scene)) {
                    listeners.forEach { it.finished() }
                    scene.onStop()
                    scene.onDestroy()
                }
            }
        }

        class Destroyed(override val scene: Scene<out Container>) : LifecycleState() {

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

            override fun replaceWith(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                w("LifecycleState", "Warning: Cannot replace scene after state is destroyed.")
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                w("LifecycleState", "Warning: Cannot finish Navigator after state is destroyed.")
                return StateTransition(this)
            }
        }
    }

    private class StateTransition(
        val newState: LifecycleState,
        val action: (() -> Unit)? = null
    )

    companion object {

        @Suppress("UNCHECKED_CAST")
        private var NavigatorState.sceneClass: KClass<out Scene<*>>?
            get() = get<String>("scene:class")?.let { Class.forName(it).kotlin as KClass<out Scene<*>>? }
            set(value) {
                set("scene:class", value?.java?.name)
            }

        private var NavigatorState.sceneState: SceneState?
            get() = get("scene:state")
            set(value) {
                set("scene:state", value)
            }
    }
}
