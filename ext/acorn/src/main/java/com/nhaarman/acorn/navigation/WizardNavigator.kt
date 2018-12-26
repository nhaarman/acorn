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
import com.nhaarman.acorn.util.lazyVar
import kotlin.reflect.KClass

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
 * This Navigator implements [SavableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by this class's saveInstanceState() method.
 */
abstract class WizardNavigator(
    private val savedState: NavigatorState?
) : Navigator, SavableNavigator, OnBackPressListener {

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
     * the instance as returned from [SavableScene.saveInstanceState] if its
     * state was saved.
     */
    protected abstract fun instantiateScene(sceneClass: KClass<out Scene<*>>, state: SceneState?): Scene<out Container>

    private var state by lazyVar {
        val size: Int? = savedState?.get("size")
        val activeIndex: Int? = savedState?.get("active_index")
        if (size == null || activeIndex == null || savedState == null) {
            val scene = createScene(0) ?: error("Initial Scene may not be null.")
            return@lazyVar State.create(listOf(scene), 0) { index -> createScene(index) }
        }

        @Suppress("UNCHECKED_CAST")
        val scenes = (0 until size)
            .map { index ->
                instantiateScene(
                    sceneClass = Class.forName(savedState["${index}_class"]).kotlin as KClass<out Scene<*>>,
                    state = savedState["${index}_state"]
                )
            }

        State.create(scenes, activeIndex) { index -> createScene(index) }
    }

    @CallSuper
    override fun addNavigatorEventsListener(listener: Navigator.Events): DisposableHandle {
        state.addListener(listener)

        (state as? State.Active)
            ?.let { state -> state.scenes[state.activeIndex] }
            ?.let { listener.scene(it, null) }

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener !in state.listeners
            }

            override fun dispose() {
                state.removeListener(listener)
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

        execute(state.next())
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

        execute(state.previous())
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

    @CallSuper
    override fun onStart() {
        v("WizardNavigator", "onStart")

        execute(state.start())
    }

    @CallSuper
    override fun onStop() {
        v("WizardNavigator", "onStop")

        execute(state.stop())
    }

    @CallSuper
    override fun onDestroy() {
        v("WizardNavigator", "onDestroy")

        execute(state.destroy())
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is State.Destroyed) return false

        v("WizardNavigator", "onBackPressed")
        if (state.activeIndex == 0) {
            execute(state.finish())
        } else {
            execute(state.previous())
        }

        return true
    }

    private fun execute(transition: StateTransition) {
        state = transition.newState
        transition.action?.invoke()
    }

    @CallSuper
    override fun saveInstanceState(): NavigatorState {
        return state.scenes
            .foldIndexed(NavigatorState()) { index, bundle, scene ->
                bundle.also {
                    it["${index}_class"] = scene::class.java.name
                    it["${index}_state"] = (scene as? SavableScene)?.saveInstanceState()
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

        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition

        abstract fun next(): StateTransition
        abstract fun previous(): StateTransition

        abstract fun finish(): StateTransition

        companion object {

            fun create(
                scenes: List<Scene<out Container>>,
                initialIndex: Int,
                factory: (Int) -> Scene<out Container>?
            ): State {
                return Inactive(scenes, initialIndex, emptyList(), factory)
            }
        }

        class Inactive(
            override val scenes: List<Scene<out Container>>,
            override val activeIndex: Int,
            override var listeners: List<Navigator.Events>,
            private val factory: (Int) -> Scene<out Container>?
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "List of Scenes may not be empty." }
                if (activeIndex >= scenes.size) throw ArrayIndexOutOfBoundsException("Scene index out of range: $activeIndex.")
            }

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): StateTransition {
                return StateTransition(Active(scenes, activeIndex, listeners, factory)) {
                    scenes[activeIndex].onStart()
                    listeners.forEach { it.scene(scenes[activeIndex], null) }
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    scenes.asReversed().forEach { it.onDestroy() }
                }
            }

            override fun next(): StateTransition {
                val newScenes = scenes.filledUpTo(activeIndex + 1, factory)

                if (newScenes == null) {
                    return StateTransition(Destroyed()) {
                        scenes.asReversed().forEach { it.onDestroy() }
                    }
                }

                return StateTransition(Inactive(newScenes, activeIndex + 1, listeners, factory))
            }

            override fun previous(): StateTransition {
                val newIndex = Math.max(0, activeIndex - 1)
                return StateTransition(Inactive(scenes, newIndex, listeners, factory))
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    listeners.forEach { it.finished() }
                    scenes.asReversed().forEach { it.onDestroy() }
                }
            }
        }

        class Active(
            override val scenes: List<Scene<out Container>>,
            override val activeIndex: Int,
            override var listeners: List<Navigator.Events>,
            private val factory: (Int) -> Scene<out Container>?
        ) : State() {

            init {
                check(scenes.isNotEmpty()) { "List of Scenes may not be empty." }
            }

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
                return StateTransition(Inactive(scenes, activeIndex, listeners, factory)) {
                    scenes[activeIndex].onStop()
                }
            }

            override fun destroy(): StateTransition {
                return stop().andThen { it.destroy() }
            }

            override fun next(): StateTransition {
                val newIndex = activeIndex + 1
                val newScenes = scenes.filledUpTo(newIndex, factory)

                if (newScenes == null) {
                    return StateTransition(Destroyed()) {
                        scenes[activeIndex].onStop()
                        scenes.asReversed().forEach { it.onDestroy() }
                        listeners.forEach { it.finished() }
                    }
                }

                return StateTransition(Active(newScenes, newIndex, listeners, factory)) {
                    scenes[activeIndex].onStop()
                    newScenes[newIndex].onStart()
                    listeners.forEach { it.scene(newScenes[newIndex], TransitionData.forwards) }
                }
            }

            override fun previous(): StateTransition {
                if (activeIndex == 0) return StateTransition(this)

                return StateTransition(Active(scenes, activeIndex - 1, listeners, factory)) {
                    scenes[activeIndex].onStop()
                    scenes[activeIndex - 1].onStart()
                    listeners.forEach { it.scene(scenes[activeIndex - 1], TransitionData.backwards) }
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Inactive(scenes, activeIndex, listeners, factory)) {
                    listeners.forEach { it.finished() }
                    scenes[activeIndex].onStop()
                }.andThen { it.destroy() }
            }
        }

        class Destroyed : State() {

            override val scenes: List<Scene<out Container>> = emptyList()
            override val activeIndex: Int = -1

            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): StateTransition {
                w("WizardNavigator.State", "Warning: Cannot start state after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(this)
            }

            override fun next(): StateTransition {
                w("WizardNavigator.State", "Warning: Cannot go to next scene after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun previous(): StateTransition {
                w("WizardNavigator.State", "Warning: Cannot go to previous scene after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                w("WizardNavigator.State", "Warning: Cannot finish navigator after navigator is destroyed.")
                return StateTransition(this)
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

    private class StateTransition(
        val newState: State,
        val action: (() -> Unit)? = null
    ) {

        fun andThen(f: (State) -> StateTransition): StateTransition {
            val newTransition = f(newState)
            return StateTransition(
                newTransition.newState
            ) {
                action?.invoke()
                newTransition.action?.invoke()
            }
        }
    }
}
