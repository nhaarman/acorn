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

import android.support.annotation.CallSuper
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.internal.v
import com.nhaarman.bravo.internal.w
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.get
import com.nhaarman.bravo.util.lazyVar
import kotlin.reflect.KClass

/**
 * An abstract [Navigator] class that uses a stack to navigate through [Scene]s.
 *
 * This Navigator supports basic [pop] and [push] operations to manipulate the
 * stack. Implementers must implement [initialStack] to provide the initial stack
 * to work with.
 *
 * This Navigator implements [SaveableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by this class's saveInstanceState() method.
 */
abstract class StackNavigator(
    private val savedState: NavigatorState?
) : Navigator, SaveableNavigator, OnBackPressListener {

    /**
     * Creates the initial stack of [Scene]s for this StackNavigator.
     *
     * The last Scene in the resulting list is regarded as the top element.
     */
    protected abstract fun initialStack(): List<Scene<out Container>>

    /**
     * Instantiates a [Scene] instance for given [sceneClass] and [state].
     *
     * This function is called when restoring the StackNavigator from a saved state.
     *
     * @param sceneClass The class of the [Scene] to instantiate.
     * @param state The saved state of the [Scene] if applicable. This will be
     * the instance as returned from [SaveableScene.saveInstanceState] if its
     * state was saved.
     */
    protected abstract fun instantiateScene(
        sceneClass: KClass<out Scene<*>>,
        state: SceneState?
    ): Scene<out Container>

    private var state by lazyVar {
        @Suppress("UNCHECKED_CAST")
        fun initialStack(): List<Scene<out Container>> {
            if (savedState == null) return this@StackNavigator.initialStack()

            val size: Int? = savedState["size"]
            if (size == null || size == 0) return this@StackNavigator.initialStack()

            return (0 until size)
                .map { index ->
                    instantiateScene(
                        sceneClass = Class.forName(savedState["${index}_class"]).kotlin as KClass<out Scene<*>>,
                        state = savedState["${index}_state"]
                    )
                }
        }

        State.create(initialStack())
    }

    @CallSuper
    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        state.addListener(listener)

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in state.listeners
            }

            override fun dispose() {
                state.removeListener(listener)
            }
        }
    }

    /**
     * Pushes given [scene] onto the stack.
     *
     * If this Navigator is currently active, the current [Scene] will be stopped,
     * and given [scene] will receive a call to [Navigator.onStart].
     *
     * If this Navigator is currently inactive, no Scene lifecycle events will
     * be called at all. Starting this Navigator will trigger a call to the
     * [Scene.onStart] method of given [scene].
     *
     * Calling this method when this Navigator has been destroyed will have no
     * effect.
     *
     * @param scene The [Scene] instance to push.
     */
    fun push(scene: Scene<out Container>) {
        v("StackNavigator", "push $scene")

        state = state.push(scene, TransitionData.forwards)
    }

    /**
     * Pops the top most [Scene] off the stack.
     *
     * If this Navigator is currently active, the current child Scene will be
     * stopped and destroyed. If the stack becomes empty, this Navigator will be
     * destroyed, otherwise the new top Scene will be started.
     *
     * If this Navigator is currently inactive and there is only one element on
     * the stack, the Navigator will be destroyed. Otherwise, the current Scene
     * will be destroyed. Starting this Navigator will trigger a call to the
     * [Scene.onStart] method of the new top Scene.
     *
     * Calling this method when the receiving Navigator has been destroyed will
     * have no effect.
     */
    fun pop() {
        v("StackNavigator", "pop")

        state = state.pop()
    }

    /**
     * Replaces the top most [Scene] with given [scene].
     *
     * If this Navigator is currently active, the current active child Scene
     * will be stopped and destroyed, and given [scene] will be started.
     *
     * If this Navigator is currently inactive, the current active child Scene
     * will be destroyed. Starting this Navigator will trigger a call to the
     * [Scene.onStart] method of given [scene].
     */
    fun replace(scene: Scene<out Container>) {
        v("StackNavigator", "replace $scene")

        state = state.replace(scene, TransitionData.forwards)
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

    @CallSuper
    override fun onStart() {
        v("StackNavigator", "onStart")

        state = state.start()
    }

    @CallSuper
    override fun onStop() {
        v("StackNavigator", "onStop")
        state = state.stop()
    }

    @CallSuper
    override fun onDestroy() {
        v("StackNavigator", "onDestroy")
        state = state.destroy()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is State.Destroyed) return false

        v("StackNavigator", "onBackPressed")
        state = state.pop()

        return true
    }

    override fun isDestroyed(): Boolean {
        return state is State.Destroyed
    }

    @CallSuper
    override fun saveInstanceState(): NavigatorState {
        return state.scenes
            .foldIndexed(NavigatorState()) { index, bundle, scene ->
                bundle.also {
                    it["${index}_class"] = scene::class.java.name
                    it["${index}_state"] = (scene as? SaveableScene)?.saveInstanceState()
                }
            }
            .also { it["size"] = state.scenes.size }
    }

    private sealed class State {

        abstract val scenes: List<Scene<out Container>>
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): State
        abstract fun stop(): State
        abstract fun destroy(): State

        abstract fun push(scene: Scene<out Container>, data: TransitionData?): State
        abstract fun pop(): State
        abstract fun replace(scene: Scene<out Container>, data: TransitionData?): State

        abstract fun finish(): State

        companion object {

            fun create(initialStack: List<Scene<out Container>>): State {
                return Inactive(initialStack, emptyList())
            }
        }

        class Inactive(
            override val scenes: List<Scene<out Container>>,
            override var listeners: List<Navigator.Events>
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "Stack may not be empty." }
            }

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): State {
                scenes.last().onStart()
                listeners.forEach { it.scene(scenes.last(), null) }
                return Active(scenes, listeners)
            }

            override fun stop(): State {
                return this
            }

            override fun finish(): State {
                listeners.forEach { it.finished() }
                return destroy()
            }

            override fun destroy(): State {
                scenes.asReversed().forEach { it.onDestroy() }
                return Destroyed()
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): State {
                return Inactive(scenes + scene, listeners)
            }

            override fun pop(): State {
                scenes.last().onDestroy()
                val newScenes = scenes.dropLast(1)

                return when {
                    newScenes.isEmpty() -> {
                        listeners.forEach { it.finished() }
                        Destroyed()
                    }
                    else -> Inactive(newScenes, listeners)
                }
            }

            override fun replace(scene: Scene<out Container>, data: TransitionData?): State {
                scenes.last().onDestroy()
                val newScenes = scenes.dropLast(1) + scene

                return Inactive(newScenes, listeners)
            }
        }

        class Active(
            override val scenes: List<Scene<out Container>>,
            override var listeners: List<Navigator.Events>
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "Stack may not be empty." }
            }

            val scene: Scene<out Container> get() = scenes.last()

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
                listener.scene(scene, null)
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): State {
                return this
            }

            override fun stop(): State {
                scenes.last().onStop()
                return Inactive(scenes, listeners)
            }

            override fun destroy(): State {
                return stop().destroy()
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): State {
                scenes.last().onStop()
                scene.onStart()
                listeners.forEach { it.scene(scene, data) }
                return Active(scenes + scene, listeners)
            }

            override fun pop(): State {
                val poppedScene = scenes.last()
                poppedScene.onStop()
                poppedScene.onDestroy()

                val newScenes = scenes.dropLast(1)

                return when {
                    newScenes.isEmpty() -> {
                        listeners.forEach { it.finished() }
                        Destroyed()
                    }
                    else -> {
                        newScenes.last().onStart()
                        listeners.forEach { it.scene(newScenes.last(), TransitionData.backwards) }
                        Active(newScenes, listeners)
                    }
                }
            }

            override fun replace(scene: Scene<out Container>, data: TransitionData?): State {
                val poppedScene = scenes.last()
                poppedScene.onStop()
                poppedScene.onDestroy()

                val newScenes = scenes.dropLast(1) + scene

                scene.onStart()
                listeners.forEach { it.scene(scene, data) }
                return Active(newScenes, listeners)
            }

            override fun finish(): State {
                listeners.forEach { it.finished() }
                return destroy()
            }
        }

        class Destroyed : State() {

            override val scenes: List<Scene<out Container>> = emptyList()
            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
                w("StackNavigator.State", "Warning: Ignoring listener for destroyed navigator.")
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): State {
                w("StackNavigator.State", "Warning: Cannot start state after navigator is destroyed.")
                return this
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                return this
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): State {
                w("StackNavigator.State", "Warning: Cannot push scene after navigator is destroyed.")
                return this
            }

            override fun pop(): State {
                w("StackNavigator.State", "Warning: Cannot pop scene after navigator is destroyed.")
                return this
            }

            override fun replace(scene: Scene<out Container>, data: TransitionData?): State {
                w("StackNavigator.State", "Warning: Cannot replace scene after navigator is destroyed.")
                return this
            }

            override fun finish(): State {
                w("StackNavigator.State", "Warning: Cannot finish navigator after navigator is destroyed.")
                return this
            }
        }
    }
}