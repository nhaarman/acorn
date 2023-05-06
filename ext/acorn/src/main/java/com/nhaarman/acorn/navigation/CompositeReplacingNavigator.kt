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
import com.nhaarman.acorn.state.navigatorState
import com.nhaarman.acorn.util.lazyVar
import kotlin.reflect.KClass

/**
 * A navigator class that can switch between [Scene]s, but has no 'back'
 * behavior of its own.
 *
 * This Navigator is able to save and restore its instance state in
 * [saveInstanceState], but does not implement [SavableNavigator] itself.
 * You can opt in to this state saving by explicitly implementing the
 * [SavableNavigator] interface.
 *
 * @param savedState An optional instance that contains saved state as returned
 * by [saveInstanceState].
 */
abstract class CompositeReplacingNavigator(
    private val savedState: NavigatorState?,
) : Navigator, Navigator.Events, OnBackPressListener {

    /**
     * Creates the initial [Navigator] for this CompositeReplacingNavigator.
     *
     * Will only be called once in the lifetime of this Navigator, and zero
     * times if this Navigator is being restored from a saved state.
     */
    protected abstract fun initialNavigator(): Navigator

    /**
     * Instantiates a [Navigator] instance for given [navigatorClass] and [state].
     *
     * This function is called when restoring the CompositeReplacingNavigator from a
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
        fun initialNavigator(): Navigator {
            val savedClass = savedState?.navigatorClass ?: return this@CompositeReplacingNavigator.initialNavigator()
            val savedState = savedState.navigatorState

            return instantiateNavigator(savedClass, savedState)
        }

        initialNavigator()
            .also { addListenerTo(it) }
            .let { LifecycleState.create(it) }
    }

    private fun addListenerTo(navigator: Navigator) {
        // Child navigators have a shorter lifetime than their parent navigators,
        // so it is not necessary to unregister the listener.
        // noinspection CheckResult
        navigator.addNavigatorEventsListener(this@CompositeReplacingNavigator)
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
     * Replaces the current [Navigator] with given [navigator].
     *
     * If the receiving Navigator is currently active, the current child
     * Navigator will be stopped and destroyed, and given [navigator] will
     * receive a call to [Navigator.onStart].
     *
     * If the receiving Navigator is currently inactive, the current child
     * Navigator will be destroyed. Starting the receiving Navigator will trigger
     * a call to the [Navigator.onStart] method of given [navigator].
     *
     * Calling this method when the receiving Navigator has been destroyed will
     * have no effect.
     */
    fun replace(navigator: Navigator) {
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
        v(this.javaClass.simpleName, "Finished")
        execute(state.finish())
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
        val navigator = state.navigator
        if (navigator !is SavableNavigator) return NavigatorState()

        return navigatorState {
            it.navigatorClass = navigator::class
            it.navigatorState = navigator.saveInstanceState()
        }
    }

    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract val navigator: Navigator?
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition
        abstract fun finish(): StateTransition

        abstract fun scene(scene: Scene<out Container>, data: TransitionData?)

        abstract fun replace(navigator: Navigator): StateTransition

        abstract fun onBackPressed(): Boolean

        companion object {

            fun create(initialNavigator: Navigator): LifecycleState {
                return Inactive(initialNavigator, emptyList(), null)
            }
        }

        class Inactive(
            override val navigator: Navigator,
            override var listeners: List<Navigator.Events>,
            private var activeScene: Scene<out Container>?,
        ) : LifecycleState() {

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): StateTransition {
                return StateTransition(Active(navigator, listeners, activeScene)) {
                    navigator.onStart()
                    activeScene?.let {
                        listeners.forEach { listener -> listener.scene(it) }
                    }
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    navigator.onDestroy()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    listeners.forEach { it.finished() }
                    navigator.onDestroy()
                }
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                this.activeScene = scene
            }

            override fun replace(navigator: Navigator): StateTransition {
                return StateTransition(Inactive(navigator, listeners, activeScene))
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Active(
            override var navigator: Navigator,
            override var listeners: List<Navigator.Events>,
            private var activeScene: Scene<out Container>?,
        ) : LifecycleState() {

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
                return StateTransition(Inactive(navigator, listeners, activeScene)) {
                    navigator.onStop()
                }
            }

            override fun destroy(): StateTransition {
                return stop().andThen { it.destroy() }
            }

            override fun finish(): StateTransition {
                return StateTransition(Inactive(navigator, listeners, activeScene)) {
                    listeners.forEach { it.finished() }
                    navigator.onStop()
                }.andThen { it.destroy() }
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
                activeScene = scene
                listeners.onEach { it.scene(scene, data) }
            }

            override fun replace(navigator: Navigator): StateTransition {
                return StateTransition(Active(navigator, listeners, activeScene)) {
                    this.navigator.onStop()
                    this.navigator.onDestroy()
                    navigator.onStart()
                }
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Destroyed : LifecycleState() {

            override val navigator: Navigator? = null
            override val listeners: List<Navigator.Events> = emptyList()

            override fun addListener(listener: Navigator.Events) {
                w("CompositeReplacingNavigator.LifecycleState", "Warning: Ignoring listener for destroyed navigator.")
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): StateTransition {
                w(
                    "CompositeReplacingNavigator.LifecycleState",
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

            override fun finish(): StateTransition {
                return StateTransition(this)
            }

            override fun scene(scene: Scene<out Container>, data: TransitionData?) {
            }

            override fun replace(navigator: Navigator): StateTransition {
                w(
                    "CompositeReplacingNavigator.LifecycleState",
                    "Warning: Cannot replace navigator after parent navigator is destroyed.",
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

    companion object {

        @Suppress("UNCHECKED_CAST")
        private var NavigatorState.navigatorClass: KClass<out Navigator>?
            get() = get<String>("navigator:class")?.let { Class.forName(it).kotlin as KClass<out Navigator>? }
            set(value) {
                set("navigator:class", value?.java?.name)
            }

        private var NavigatorState.navigatorState: NavigatorState?
            get() = get("navigator:state")
            set(value) {
                set("navigator:state", value)
            }
    }
}
