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

/**
 * An abstract [Navigator] class that is able to go back and forth through a
 * list of [Scene]s.
 *
 * This Navigator has two methods [next] and [previous] to navigate through the
 * Scenes. Calling [previous] when the first Scene is being shown will have no
 * effect, calling [next] when the last Scene is being shown will finish this
 * Navigator.
 *
 * Implementers must implement [createScene] to provide the proper Scenes.
 *
 * This Navigator implements [SaveableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by this class's saveInstanceState() method.
 */
abstract class WizardNavigator<E : Navigator.Events>(
    private val savedState: NavigatorState?
) : Navigator<E>, SaveableNavigator, OnBackPressListener {

    /**
     * Creates the Scene for given [index], starting at `0`.
     *
     * This method will be called up to once for each index, results will be
     * reused when navigating through the wizard.
     *
     * @return the created [Scene], or `null` if the end of the wizard is reached.
     */
    protected abstract fun createScene(index: Int): Scene<out Container>?

    /**
     * Instantiates a [Scene] instance for given [sceneClass] and [state].
     *
     * This function is called when restoring the StackNavigator from a saved state.
     *
     * @param sceneClass The class of the [Scene] to instantiate.
     * @param state The saved state of the [Scene] if applicable. This will be
     *              the instance as returned from [StateSaveable.saveInstanceState]
     *              if its state was saved.
     */
    abstract fun instantiateScene(sceneClass: Class<*>, state: SceneState?): Scene<out Container>

    private var state by lazyVar {
        val size: Int? = savedState?.get("size")
        val activeIndex: Int? = savedState?.get("active_index")
        if (size == null || activeIndex == null || savedState == null) {
            val scene = createScene(0) ?: error("Initial Scene may not be null.")
            return@lazyVar State.create(listOf(scene), 0) { index -> createScene(index) }
        }

        val scenes = (0 until size)
            .map { index ->
                instantiateScene(
                    sceneClass = Class.forName(savedState["${index}_class"]),
                    state = savedState["${index}_state"]
                )
            }

        State.create(scenes, activeIndex) { index -> createScene(index) }
    }

    /**
     * The list of [E] instances that have registered with this Navigator.
     *
     * @see addListener
     */
    protected val listeners: List<E> get() = _listeners

    private val _listeners = mutableListOf<E>()

    @CallSuper
    override fun addListener(listener: E): DisposableHandle {
        _listeners += listener

        (state as? State.Active)
            ?.let { state -> state.scenes[state.activeIndex] }
            ?.let { listener.scene(it, null) }

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in _listeners
            }

            override fun dispose() {
                _listeners -= listener
            }
        }
    }

    /**
     * Navigates to the next [Scene] in this wizard if possible.
     *
     * If there is no next Scene, this Navigator will finish and all Scenes will
     * be destroyed.
     *
     * If there is a next Scene and this Navigator is currently active, the
     * current [Scene] will be stopped, and the next Scene will be started.
     *
     * If there is a next Scene and this Navigator is currently inactive, no Scene
     * lifecycle events will be called at all. Starting this Navigator will trigger
     * a call to the [Scene.onStart] of the next Scene in the wizard.
     *
     * Calling this method when this Navigator has been destroyed will have no
     * effect.
     */
    fun next() {
        v("WizardNavigator", "next")

        state = state.next()
        notifyListenersOfState(TransitionData.forwards)
    }

    /**
     * Navigates to the next [Scene] in this wizard if possible.
     *
     * If there is no previous Scene, nothing will happen.
     *
     * If there is a previous Scene and this Navigator is currently active, the
     * current [Scene] will be stopped, and the previous Scene will be started.
     *
     * If there is a previous Scene and this Navigator is currently inactive, no
     * Scene lifecycle events will be called at all. Starting this Navigator will
     * trigger a call to the [Scene.onStart] of the previous Scene in the wizard.
     *
     * Calling this method when this Navigator has been destroyed will have no
     * effect.
     */
    fun previous() {
        v("WizardNavigator", "previous")

        state = state.previous()
        notifyListenersOfState(TransitionData.backwards)
    }

    @CallSuper
    override fun onStart() {
        v("WizardNavigator", "onStart")

        state = state.start()
        notifyListenersOfState(null)
    }

    @CallSuper
    override fun onStop() {
        v("WizardNavigator", "onStop")

        state = state.stop()
    }

    @CallSuper
    override fun onDestroy() {
        v("WizardNavigator", "onDestroy")

        state = state.destroy()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        v("WizardNavigator", "onBackPressed")
        if (state.activeIndex == 0) {
            state = state.destroy()
        } else {
            state = state.previous()
        }

        notifyListenersOfState(TransitionData.backwards)

        return true
    }

    private fun notifyListenersOfState(data: TransitionData?) {
        state.let { state ->
            when (state) {
                is State.Inactive -> Unit
                is State.Active -> state.scenes[state.activeIndex].let { scene ->
                    _listeners.forEach {
                        it.scene(scene, data)
                    }
                }
                is State.Destroyed -> _listeners.forEach { it.finished() }
            }
        }
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
            .also {
                it["size"] = state.scenes.size
                it["active_index"] = state.activeIndex
            }
    }

    override fun isDestroyed(): Boolean {
        return state is State.Destroyed
    }

    private sealed class State {

        abstract val scenes: List<Scene<out Container>>
        abstract val activeIndex: Int

        abstract fun start(): State
        abstract fun stop(): State
        abstract fun destroy(): State

        abstract fun next(): State
        abstract fun previous(): State

        companion object {

            fun create(
                scenes: List<Scene<out Container>>,
                initialIndex: Int,
                factory: (Int) -> Scene<out Container>?
            ): State {
                return Inactive(scenes, initialIndex, factory)
            }
        }

        class Inactive(
            override val scenes: List<Scene<out Container>>,
            override val activeIndex: Int,
            private val factory: (Int) -> Scene<out Container>?
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "List of Scenes may not be empty." }
                if (activeIndex >= scenes.size) throw ArrayIndexOutOfBoundsException("Scene index out of range: $activeIndex.")
            }

            override fun start(): State {
                scenes[activeIndex].onStart()
                return Active(scenes, activeIndex, factory)
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                scenes.asReversed().forEach { it.onDestroy() }
                return Destroyed()
            }

            override fun next(): State {
                val newScenes = scenes.filledUpTo(activeIndex + 1, factory)

                if (newScenes == null) {
                    scenes.asReversed().forEach { it.onDestroy() }
                    return Destroyed()
                }

                return Inactive(newScenes, activeIndex + 1, factory)
            }

            override fun previous(): State {
                val newIndex = Math.max(0, activeIndex - 1)
                return Inactive(scenes, newIndex, factory)
            }
        }

        class Active(
            override val scenes: List<Scene<out Container>>,
            override val activeIndex: Int,
            private val factory: (Int) -> Scene<out Container>?
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "List of Scenes may not be empty." }
            }

            override fun start(): State {
                return this
            }

            override fun stop(): State {
                scenes[activeIndex].onStop()
                return Inactive(scenes, activeIndex, factory)
            }

            override fun destroy(): State {
                return stop().destroy()
            }

            override fun next(): State {
                scenes[activeIndex].onStop()
                val newScenes = scenes.filledUpTo(activeIndex + 1, factory)

                if (newScenes == null) {
                    scenes.asReversed().forEach { it.onDestroy() }
                    return Destroyed()
                }

                newScenes[activeIndex + 1].onStart()
                return Active(newScenes, activeIndex + 1, factory)
            }

            override fun previous(): State {
                if (activeIndex == 0) return this

                scenes[activeIndex].onStop()
                scenes[activeIndex - 1].onStart()
                return Active(scenes, activeIndex - 1, factory)
            }
        }

        class Destroyed : State() {

            override val scenes: List<Scene<out Container>> = emptyList()
            override val activeIndex: Int = -1

            override fun start(): State {
                w("WizardNavigator.State", "Warning: Cannot start state after navigator is destroyed.")
                return this
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                return this
            }

            override fun next(): State {
                w("WizardNavigator.State", "Warning: Cannot go to next scene after navigator is destroyed.")
                return this
            }

            override fun previous(): State {
                w("WizardNavigator.State", "Warning: Cannot go to previous scene after navigator is destroyed.")
                return this
            }
        }

        protected fun <T> List<T>.filledUpTo(index: Int, f: (Int) -> T?): List<T>? {
            if (size > index) return this

            return (size..index)
                .fold<Int, List<T>?>(this) { list, i ->
                    f(i)?.let { t -> list?.let { it + t } }
                }
        }
    }
}
