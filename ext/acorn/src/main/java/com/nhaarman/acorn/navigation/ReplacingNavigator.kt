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
import kotlin.reflect.KClass

/**
 * A navigator class that can switch between [Scene]s, but has no 'back'
 * behavior.
 */
abstract class ReplacingNavigator(
    private val savedState: NavigatorState?
) : Navigator, SavableNavigator, OnBackPressListener {

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
        state = state.replaceWith(newScene)

        if (state is LifecycleState.Active) {
            state.listeners.forEach { it.scene(state.scene, data) }
        }
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
        state = state.finish()
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
                return listener in state.listeners
            }

            override fun dispose() {
                state.removeListener(listener)
            }
        }
    }

    @CallSuper
    override fun onStart() {
        v("ReplacingNavigator", "onStart")

        state = state.start()
        state.listeners.forEach { it.scene(state.scene) }
    }

    @CallSuper
    override fun onStop() {
        v("ReplacingNavigator", "onStop")
        state = state.stop()
    }

    @CallSuper
    override fun onDestroy() {
        v("ReplacingNavigator", "onDestroy")
        state = state.destroy()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is LifecycleState.Destroyed) return false

        v("ReplacingNavigator", "onBackPressed")
        state = state.finish()

        return true
    }

    @CallSuper
    override fun saveInstanceState(): NavigatorState {
        return navigatorState {
            it.sceneClass = state.scene::class
            it.sceneState = (state.scene as? SavableScene)?.saveInstanceState()
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

        abstract fun start(): LifecycleState
        abstract fun stop(): LifecycleState
        abstract fun destroy(): LifecycleState

        abstract fun replaceWith(scene: Scene<out Container>): LifecycleState
        abstract fun finish(): LifecycleState

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

            override fun start(): LifecycleState {
                scene.onStart()
                return Active(scene, listeners)
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                scene.onDestroy()
                return Destroyed(scene)
            }

            override fun replaceWith(scene: Scene<out Container>): LifecycleState {
                return Inactive(scene, listeners)
            }

            override fun finish(): LifecycleState {
                listeners.forEach { it.finished() }
                return destroy()
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
                return Destroyed(scene)
            }

            override fun replaceWith(scene: Scene<out Container>): LifecycleState {
                this.scene.onStop()
                this.scene.onDestroy()
                scene.onStart()
                return Active(scene, listeners)
            }

            override fun finish(): LifecycleState {
                listeners.forEach { it.finished() }
                return destroy()
            }
        }

        class Destroyed(override val scene: Scene<out Container>) : LifecycleState() {

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

            override fun replaceWith(scene: Scene<out Container>): LifecycleState {
                w("LifecycleState", "Warning: Cannot replace scene after state is destroyed.")
                return this
            }

            override fun finish(): LifecycleState {
                w("LifecycleState", "Warning: Cannot finish Navigator after state is destroyed.")
                return this
            }
        }
    }

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