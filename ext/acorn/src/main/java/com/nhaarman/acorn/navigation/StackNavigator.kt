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
 * An abstract [Navigator] class that uses a stack to navigate through [Scene]s.
 *
 * This Navigator supports basic [pop] and [push] operations to manipulate the
 * stack. Implementers must implement [initialStack] to provide the initial stack
 * to work with.
 *
 * This Navigator is able to save and restore its instance state in
 * [saveInstanceState], but does not implement [SavableNavigator] itself.
 * You can opt in to this state saving by explicitly implementing the
 * [SavableNavigator] interface.
 *
 * @param savedState An optional instance that contains saved state as returned
 * by [saveInstanceState].
 */
abstract class StackNavigator(
    private val savedState: NavigatorState?
) : Navigator, OnBackPressListener {

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
     * the instance as returned from [SavableScene.saveInstanceState] if its
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
                return listener !in state.listeners
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
     * and given [scene] will receive a call to [Scene.onStart].
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

        execute(state.push(scene, TransitionData.forwards))
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

        execute(state.pop())
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

        execute(state.replace(scene, TransitionData.forwards))
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
        v("StackNavigator", "finish")

        execute(state.finish())
    }

    @CallSuper
    override fun onStart() {
        v("StackNavigator", "onStart")

        execute(state.start())
    }

    @CallSuper
    override fun onStop() {
        v("StackNavigator", "onStop")
        execute(state.stop())
    }

    @CallSuper
    override fun onDestroy() {
        v("StackNavigator", "onDestroy")
        execute(state.destroy())
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (state is State.Destroyed) return false

        v("StackNavigator", "onBackPressed")
        execute(state.pop())

        return true
    }

    private fun execute(transition: StateTransition) {
        state = transition.newState
        transition.action?.invoke()
    }

    override fun isDestroyed(): Boolean {
        return state is State.Destroyed
    }

    @CallSuper
    open fun saveInstanceState(): NavigatorState {
        val savableStack = state.scenes.takeWhile { it is SavableScene }
        return savableStack
            .foldIndexed(NavigatorState()) { index, bundle, scene ->
                bundle.also {
                    it["${index}_class"] = scene::class.java.name
                    it["${index}_state"] = (scene as? SavableScene)?.saveInstanceState()
                }
            }
            .also { it["size"] = savableStack.size }
    }

    private sealed class State {

        abstract val scenes: List<Scene<out Container>>
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition

        abstract fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition
        abstract fun pop(): StateTransition
        abstract fun replace(scene: Scene<out Container>, data: TransitionData?): StateTransition

        abstract fun finish(): StateTransition

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

            override fun start(): StateTransition {
                return StateTransition(Active(scenes, listeners)) {
                    listeners.forEach { it.scene(scenes.last(), null) }
                    scenes.last().onStart()
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    listeners.forEach { it.finished() }
                    scenes.asReversed().forEach { it.onDestroy() }
                }
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    scenes.asReversed().forEach { it.onDestroy() }
                }
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                return StateTransition(Inactive(scenes + scene, listeners))
            }

            override fun pop(): StateTransition {
                val newScenes = scenes.dropLast(1)

                return when {
                    newScenes.isEmpty() -> StateTransition(Destroyed()) {
                        scenes.last().onDestroy()
                        listeners.forEach { it.finished() }
                    }
                    else -> StateTransition(Inactive(newScenes, listeners)) {
                        scenes.last().onDestroy()
                    }
                }
            }

            override fun replace(
                scene: Scene<out Container>,
                data: TransitionData?
            ): StateTransition {
                val newScenes = scenes.dropLast(1) + scene

                return StateTransition(Inactive(newScenes, listeners)) {
                    scenes.last().onDestroy()
                }
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

            override fun start(): StateTransition {
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(Inactive(scenes, listeners)) {
                    scenes.last().onStop()
                }
            }

            override fun destroy(): StateTransition {
                return stop().andThen { it.destroy() }
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                return StateTransition(Active(scenes + scene, listeners)) {
                    scenes.last().onStop()
                    listeners.forEach { it.scene(scene, data) }
                    scene.onStart()
                }
            }

            override fun pop(): StateTransition {
                val poppedScene = scenes.last()
                val newScenes = scenes.dropLast(1)

                return when {
                    newScenes.isEmpty() -> StateTransition(Destroyed()) {
                        poppedScene.onStop()
                        poppedScene.onDestroy()

                        listeners.forEach { it.finished() }
                    }
                    else -> StateTransition(Active(newScenes, listeners)) {
                        poppedScene.onStop()
                        poppedScene.onDestroy()

                        listeners.forEach { it.scene(newScenes.last(), TransitionData.backwards) }
                        newScenes.last().onStart()
                    }
                }
            }

            override fun replace(
                scene: Scene<out Container>,
                data: TransitionData?
            ): StateTransition {
                val poppedScene = scenes.last()
                val newScenes = scenes.dropLast(1) + scene

                return StateTransition(Active(newScenes, listeners)) {
                    poppedScene.onStop()
                    poppedScene.onDestroy()

                    listeners.forEach { it.scene(scene, data) }
                    scene.onStart()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Inactive(scenes, listeners)) {
                    listeners.forEach { it.finished() }
                    scenes.last().onStop()
                }.andThen { it.destroy() }
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

            override fun start(): StateTransition {
                w(
                    "StackNavigator.State",
                    "Warning: Cannot start state after navigator is destroyed."
                )
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(this)
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                w(
                    "StackNavigator.State",
                    "Warning: Cannot push scene after navigator is destroyed."
                )
                return StateTransition(this)
            }

            override fun pop(): StateTransition {
                w("StackNavigator.State", "Warning: Cannot pop scene after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun replace(
                scene: Scene<out Container>,
                data: TransitionData?
            ): StateTransition {
                w(
                    "StackNavigator.State",
                    "Warning: Cannot replace scene after navigator is destroyed."
                )
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                w(
                    "StackNavigator.State",
                    "Warning: Cannot finish navigator after navigator is destroyed."
                )
                return StateTransition(this)
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
