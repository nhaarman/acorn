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
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.util.lazyVar
import kotlin.reflect.KClass

/**
 * An abstract [Navigator] class that uses a stack to navigate through [Navigator]s.
 *
 * Like [StackNavigator] this class supports basic [pop] and [push] operations
 * to manipulate the stack. Implementers must implement [initialStack] to provide
 * the initial stack to work with.
 *
 * This Navigator is able to save and restore its instance state in
 * [saveInstanceState], but does not implement [SavableNavigator] itself.
 * You can opt in to this state saving by explicitly implementing the
 * [SavableNavigator] interface.
 *
 * @param savedState An optional instance that contains saved state as returned
 * by [saveInstanceState].
 */
abstract class CompositeStackNavigator(
    private val savedState: NavigatorState?,
) : Navigator, Navigator.Events, OnBackPressListener {

    /**
     * Creates the initial stack of [Navigator]s for this CompositeStackNavigator.
     *
     * The last Navigator in the resulting list is regarded as the top element.
     */
    protected abstract fun initialStack(): List<Navigator>

    /**
     * Instantiates a [Navigator] instance for given [navigatorClass] and [state].
     *
     * This function is called when restoring the CompositeStackNavigator from a
     * saved state.
     *
     * @param navigatorClass The Class of the [Navigator] to instantiate.
     * @param state The saved state of the [Navigator] if applicable. This will
     * be the instance as returned from [SavableNavigator.saveInstanceState] if
     * its state was saved.
     */
    protected abstract fun instantiateNavigator(
        navigatorClass: KClass<out Navigator>,
        state: NavigatorState?,
    ): Navigator

    private var state by lazyVar {
        @Suppress("UNCHECKED_CAST")
        fun initialStack(): List<Navigator> {
            if (savedState == null) return this@CompositeStackNavigator.initialStack()

            val size: Int? = savedState.get("size")
            if (size == null || size == 0) return this@CompositeStackNavigator.initialStack()

            return (0 until size)
                .map { index ->
                    instantiateNavigator(
                        navigatorClass = Class.forName(savedState["${index}_class"]).kotlin as KClass<out Navigator>,
                        state = savedState["${index}_state"],
                    )
                }
        }

        initialStack()
            .also { stack -> stack.forEach { addListenerTo(it) } }
            .let { LifecycleState.create(it) }
    }

    private fun addListenerTo(navigator: Navigator) {
        // Child navigators have a shorter lifetime than their parent navigators,
        // so it is not necessary to unregister the listener.
        // noinspection CheckResult
        navigator.addNavigatorEventsListener(this@CompositeStackNavigator)
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
     * Pushes given [navigator] onto the stack.
     *
     * If the receiving Navigator is currently active, the current child
     * Navigator will be stopped, and given [navigator] will receive a call to
     * [Navigator.onStart].
     *
     * If the receiving Navigator is currently inactive, no Navigator lifecycle
     * events will be called at all. Starting the receiving Navigator will trigger
     * a call to the [Navigator.onStart] method of given [navigator].
     *
     * Calling this method when the receiving Navigator has been destroyed will
     * have no effect.
     */
    fun push(navigator: Navigator) {
        v(this.javaClass.simpleName, "push $navigator")

        addListenerTo(navigator)
        execute(state.push(navigator))
    }

    /**
     * Pops the top most Navigator off the stack.
     *
     * If the receiving Navigator is currently active, the current child
     * Navigator will be stopped and destroyed. If the stack becomes empty, the
     * receiving Navigator will be destroyed, otherwise the new top Navigator
     * will be started.
     *
     * If the receiving Navigator is currently inactive and there is only one
     * element on the stack, the Navigator will be destroyed. Otherwise, no
     * events will be called at all. Starting the receiving Navigator will
     * trigger a call to the [Navigator.onStart] method of the new top Navigator.
     *
     * Calling this method when the receiving Navigator has been destroyed will
     * have no effect.
     */
    fun pop() {
        v(this.javaClass.simpleName, "pop")

        execute(state.pop())
    }

    /**
     * Replaces the current active [Navigator] with given [navigator].
     *
     * If the receiving Navigator is currently active, the current child
     * Navigator will be stopped, and given [navigator] will receive a call to
     * [Navigator.onStart].
     *
     * If the receiving Navigator is currently inactive, the current top child
     * Navigator will be destroyed.Starting the receiving Navigator will trigger
     * a call to the [Navigator.onStart] method of given [navigator].
     *
     * Calling this method when the receiving Navigator has been destroyed will
     * have no effect.
     */
    fun replace(navigator: Navigator) {
        v(this.javaClass.simpleName, "replace $navigator")

        addListenerTo(navigator)
        execute(state.replace(navigator))
    }

    /**
     * Finishes this Navigator.
     *
     * If this Navigator is currently active, the current child Navigator will
     * be stopped and destroyed, and the receiving Navigator will be destroyed.
     *
     * If this Navigator is currently not active, the current navigator will only
     * have its [Scene.onDestroy] method called.
     *
     * Calling this method when the Navigator has been destroyed will have no
     * effect.
     */
    fun finish() {
        v(this.javaClass.simpleName, "finish")

        execute(state.finish())
    }

    @CallSuper
    override fun onStart() {
        v(this.javaClass.simpleName, "onStart")
        execute(state.start())
    }

    @CallSuper
    override fun onStop() {
        v(this.javaClass.simpleName, "onStop")
        execute(state.stop())
    }

    @CallSuper
    override fun onDestroy() {
        v(this.javaClass.simpleName, "onDestroy")
        execute(state.destroy())
    }

    @CallSuper
    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
        v(this.javaClass.simpleName, "Scene change: $scene, $data")
        state.scene(scene, data)
    }

    @CallSuper
    override fun finished() {
        execute(state.pop())
    }

    private fun execute(transition: StateTransition) {
        state = transition.newState
        transition.action?.invoke()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        v(this.javaClass.simpleName, "onBackPressed")
        return state.onBackPressed()
    }

    @CallSuper
    open fun saveInstanceState(): NavigatorState {
        val savableStack = state.navigators.takeWhile { it is SavableNavigator }

        return savableStack
            .foldIndexed(NavigatorState()) { index, bundle, navigator ->
                bundle.also {
                    it["${index}_class"] = navigator::class.java.name
                    it["${index}_state"] = (navigator as? SavableNavigator)?.saveInstanceState()
                }
            }
            .also { it["size"] = savableStack.size }
    }

    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract val navigators: List<Navigator>
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition

        abstract fun scene(scene: Scene<out Container>, data: TransitionData?)

        abstract fun push(navigator: Navigator): StateTransition
        abstract fun pop(): StateTransition
        abstract fun replace(navigator: Navigator): StateTransition
        abstract fun finish(): StateTransition

        abstract fun onBackPressed(): Boolean

        companion object {

            fun create(initialStack: List<Navigator>): LifecycleState {
                return Inactive(initialStack, emptyList())
            }
        }

        class Inactive(
            override val navigators: List<Navigator>,
            override var listeners: List<Navigator.Events>,
        ) : LifecycleState() {

            init {
                check(navigators.isNotEmpty()) { "Stack may not be empty." }
            }

            private val navigator: Navigator get() = navigators.last()

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): StateTransition {
                return StateTransition(Active(navigators, listeners)) {
                    navigators.last().onStart()
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    navigators.asReversed().forEach { it.onDestroy() }
                }
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            }

            override fun push(navigator: Navigator): StateTransition {
                return StateTransition(Inactive(navigators + navigator, listeners))
            }

            override fun pop(): StateTransition {
                val newScenes = navigators.dropLast(1)

                return when {
                    newScenes.isEmpty() -> StateTransition(Destroyed()) {
                        navigators.last().onDestroy()
                        listeners.forEach { it.finished() }
                    }
                    else -> StateTransition(Inactive(newScenes, listeners)) {
                        navigators.last().onDestroy()
                    }
                }
            }

            override fun replace(navigator: Navigator): StateTransition {
                val newScenes = navigators.dropLast(1) + navigator
                return StateTransition(Inactive(newScenes, listeners)) {
                    navigators.last().onDestroy()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    listeners.forEach { it.finished() }
                    navigators.asReversed().forEach { it.onDestroy() }
                }
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Active(
            override var navigators: List<Navigator>,
            override var listeners: List<Navigator.Events>,
        ) : LifecycleState() {

            private var activeScene: Scene<out Container>? = null

            init {
                check(navigators.isNotEmpty()) { "Stack may not be empty." }
            }

            val navigator: Navigator get() = navigators.last()

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
                activeScene?.let { listener.scene(it, null) }
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): StateTransition {
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(Inactive(navigators, listeners)) {
                    navigators.last().onStop()
                }
            }

            override fun destroy(): StateTransition {
                return stop().andThen { it.destroy() }
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                val newData = when (backward) {
                    null -> data
                    else -> TransitionData.create(backward ?: false)
                }

                backward = null

                activeScene = scene
                listeners.onEach { it.scene(scene, newData) }
            }

            private var backward: Boolean? = null
            override fun push(navigator: Navigator): StateTransition {
                backward = false

                return StateTransition(this) {
                    navigators.last().onStop()
                    navigators += navigator
                    navigator.onStart()
                }
            }

            override fun pop(): StateTransition {
                backward = true

                val newNavigators = navigators.dropLast(1)

                return when {
                    newNavigators.isEmpty() -> StateTransition(Destroyed()) {
                        val poppedNavigator = navigators.last()
                        poppedNavigator.onStop()
                        poppedNavigator.onDestroy()

                        listeners.forEach { it.finished() }
                    }
                    else -> StateTransition(this) {
                        val poppedNavigator = navigators.last()
                        poppedNavigator.onStop()
                        poppedNavigator.onDestroy()

                        newNavigators.last().onStart()
                        navigators = newNavigators
                    }
                }
            }

            override fun replace(navigator: Navigator): StateTransition {
                backward = false

                return StateTransition(this) {
                    val poppedNavigator = navigators.last()
                    poppedNavigator.onStop()
                    poppedNavigator.onDestroy()

                    val newNavigators = navigators.dropLast(1) + navigator

                    navigator.onStart()
                    navigators = newNavigators
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Inactive(navigators, listeners)) {
                    listeners.forEach { it.finished() }
                    navigators.last().onStop()
                }.andThen { destroy() }
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Destroyed : LifecycleState() {

            override val navigators: List<Navigator> = emptyList()
            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Ignoring listener for destroyed navigator.",
                )
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): StateTransition {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot start state after navigator is destroyed.",
                )
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(this)
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            }

            override fun push(navigator: Navigator): StateTransition {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot push navigator after parent navigator is destroyed.",
                )
                return StateTransition(this)
            }

            override fun pop(): StateTransition {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot pop scene after navigator is destroyed.",
                )
                return StateTransition(this)
            }

            override fun replace(navigator: Navigator): StateTransition {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot replace scene after navigator is destroyed.",
                )
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot finish navigator after navigator is destroyed.",
                )
                return StateTransition(this)
            }

            override fun onBackPressed(): Boolean {
                return false
            }
        }
    }

    private class StateTransition(
        val newState: LifecycleState,
        val action: (() -> Unit)? = null,
    ) {

        fun andThen(f: (LifecycleState) -> StateTransition): StateTransition {
            val newTransition = f(newState)
            return StateTransition(
                newTransition.newState,
            ) {
                action?.invoke()
                newTransition.action?.invoke()
            }
        }
    }
}
