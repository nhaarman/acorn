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
 * This Navigator implements [SaveableNavigator] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by this class's saveInstanceState() method.
 */
abstract class CompositeStackNavigator(
    private val savedState: NavigatorState?
) : Navigator, Navigator.Events, SaveableNavigator, OnBackPressListener {

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
     * be the instance as returned from [SaveableNavigator.saveInstanceState] if
     * its state was saved.
     */
    protected abstract fun instantiateNavigator(
        navigatorClass: KClass<out Navigator>,
        state: NavigatorState?
    ): Navigator

    private var state by lazyVar {
        @Suppress("UNCHECKED_CAST")
        fun initialStack(): List<Navigator> {
            val size: Int = savedState?.get("size") ?: return this@CompositeStackNavigator.initialStack()

            return (0 until size)
                .map { index ->
                    instantiateNavigator(
                        navigatorClass = Class.forName(savedState["${index}_class"]).kotlin as KClass<out Navigator>,
                        state = savedState["${index}_state"]
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
                return listener in state.listeners
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
        state = state.push(navigator)
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

        state = state.pop()
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
        state = state.replace(navigator)
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

        state = state.finish()
    }

    @CallSuper
    override fun onStart() {
        v(this.javaClass.simpleName, "onStart")
        state = state.start()
    }

    @CallSuper
    override fun onStop() {
        v(this.javaClass.simpleName, "onStop")
        state = state.stop()
    }

    @CallSuper
    override fun onDestroy() {
        v(this.javaClass.simpleName, "onDestroy")
        state = state.destroy()
    }

    @CallSuper
    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
        v(this.javaClass.simpleName, "Scene change: $scene, $data")
        state.scene(scene, data)
    }

    @CallSuper
    override fun finished() {
        state = state.pop()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        v(this.javaClass.simpleName, "onBackPressed")
        return state.onBackPressed()
    }

    @CallSuper
    override fun saveInstanceState(): NavigatorState {
        return state.navigators
            .foldIndexed(NavigatorState()) { index, bundle, navigator ->
                bundle.also {
                    it["${index}_class"] = navigator::class.java.name
                    it["${index}_state"] = (navigator as? SaveableNavigator)?.saveInstanceState()
                }
            }
            .also { it["size"] = state.navigators.size }
    }

    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract val navigators: List<Navigator>
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): LifecycleState
        abstract fun stop(): LifecycleState
        abstract fun destroy(): LifecycleState

        abstract fun scene(scene: Scene<out Container>, data: TransitionData?)

        abstract fun push(navigator: Navigator): LifecycleState
        abstract fun pop(): LifecycleState
        abstract fun replace(navigator: Navigator): LifecycleState
        abstract fun finish(): LifecycleState

        abstract fun onBackPressed(): Boolean

        companion object {

            fun create(initialStack: List<Navigator>): LifecycleState {
                return Inactive(initialStack, emptyList(), null)
            }
        }

        class Inactive(
            override val navigators: List<Navigator>,
            override var listeners: List<Navigator.Events>,
            private var activeScene: Scene<out Container>?
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

            override fun start(): LifecycleState {
                navigators.last().onStart()
                return Active(navigators, listeners, activeScene)
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                navigators.asReversed().forEach { it.onDestroy() }
                return Destroyed()
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                this.activeScene = scene
            }

            override fun push(navigator: Navigator): LifecycleState {
                return Inactive(navigators + navigator, listeners, activeScene)
            }

            override fun pop(): LifecycleState {
                navigators.last().onDestroy()
                val newScenes = navigators.dropLast(1)

                return when {
                    newScenes.isEmpty() -> {
                        listeners.forEach { it.finished() }
                        Destroyed()
                    }
                    else -> Inactive(newScenes, listeners, activeScene)
                }
            }

            override fun replace(navigator: Navigator): LifecycleState {
                navigators.last().onDestroy()
                val newScenes = navigators.dropLast(1) + navigator

                return Inactive(newScenes, listeners, activeScene)
            }

            override fun finish(): LifecycleState {
                listeners.forEach { it.finished() }
                return destroy()
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Active(
            override var navigators: List<Navigator>,
            override var listeners: List<Navigator.Events>,
            private var activeScene: Scene<out Container>?
        ) : LifecycleState() {

            init {
                check(navigators.isNotEmpty()) { "Stack may not be empty." }

                activeScene?.let {
                    listeners.forEach { listener ->
                        listener.scene(it, null)
                    }
                }
            }

            val navigator: Navigator get() = navigators.last()

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
                activeScene?.let { listener.scene(it, null) }
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): LifecycleState {
                return this
            }

            override fun stop(): LifecycleState {
                navigators.last().onStop()
                return Inactive(navigators, listeners, activeScene)
            }

            override fun destroy(): LifecycleState {
                return stop().destroy()
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
            override fun push(navigator: Navigator): LifecycleState {
                backward = false

                navigators.last().onStop()
                navigator.onStart()
                navigators += navigator
                return this
            }

            override fun pop(): LifecycleState {
                backward = true

                val poppedNavigator = navigators.last()
                poppedNavigator.onStop()
                poppedNavigator.onDestroy()

                val newNavigators = navigators.dropLast(1)

                return when {
                    newNavigators.isEmpty() -> {
                        listeners.forEach { it.finished() }
                        Destroyed()
                    }
                    else -> {
                        newNavigators.last().onStart()
                        navigators = newNavigators
                        this
                    }
                }
            }

            override fun replace(navigator: Navigator): LifecycleState {
                backward = false

                val poppedNavigator = navigators.last()
                poppedNavigator.onStop()
                poppedNavigator.onDestroy()

                val newNavigators = navigators.dropLast(1) + navigator

                navigator.onStart()
                navigators = newNavigators
                return this
            }

            override fun finish(): LifecycleState {
                listeners.forEach { it.finished() }
                return destroy()
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Destroyed : LifecycleState() {

            override val navigators: List<Navigator> = emptyList()
            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
                w("CompositeStackNavigator.LifecycleState", "Warning: Ignoring listener for destroyed navigator.")
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): LifecycleState {
                w("CompositeStackNavigator.LifecycleState", "Warning: Cannot start state after navigator is destroyed.")
                return this
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                return this
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            }

            override fun push(navigator: Navigator): LifecycleState {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot push navigator after parent navigator is destroyed."
                )
                return this
            }

            override fun pop(): LifecycleState {
                w("CompositeStackNavigator.LifecycleState", "Warning: Cannot pop scene after navigator is destroyed.")
                return this
            }

            override fun replace(navigator: Navigator): LifecycleState {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot replace scene after navigator is destroyed."
                )
                return this
            }

            override fun finish(): LifecycleState {
                w(
                    "CompositeStackNavigator.LifecycleState",
                    "Warning: Cannot finish navigator after navigator is destroyed."
                )
                return this
            }

            override fun onBackPressed(): Boolean {
                return false
            }
        }
    }
}
