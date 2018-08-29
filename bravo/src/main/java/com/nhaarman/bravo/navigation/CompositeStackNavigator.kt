package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.internal.v
import com.nhaarman.bravo.internal.w
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.util.lazyVar

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
abstract class CompositeStackNavigator<E : Navigator.Events>(
    private val savedState: NavigatorState?
) : Navigator<E>, Navigator.Events, SaveableNavigator, OnBackPressListener {

    /**
     * Creates the initial stack of [Navigator]s for this CompositeStackNavigator.
     *
     * The last Navigator in the resulting list is regarded as the top element.
     */
    protected abstract fun initialStack(): List<Navigator<out Navigator.Events>>

    /**
     * Instantiates a [Navigator] instance for given [navigatorClass] and [state].
     *
     * This function is called when restoring the CompositeStackNavigator from a
     * saved state.
     *
     * @param navigatorClass The Class of the [Navigator] to instantiate.
     * @param state The saved state of the [Navigator] if applicable. This will
     *              be the instance as returned from [StateSaveable.saveInstanceState]
     *              if its state was saved.
     */
    abstract fun instantiateNavigator(
        navigatorClass: Class<Navigator<*>>,
        state: NavigatorState?
    ): Navigator<out Navigator.Events>

    private var state by lazyVar {
        @Suppress("UNCHECKED_CAST")
        fun initialStack(): List<Navigator<out Navigator.Events>> {
            val size: Int = savedState?.get("size") ?: return this@CompositeStackNavigator.initialStack()

            return (0 until size)
                .map { index ->
                    instantiateNavigator(
                        navigatorClass = Class.forName(savedState["${index}_class"]) as Class<Navigator<*>>,
                        state = savedState["${index}_state"]
                    )
                }
        }

        initialStack()
            .also { stack -> stack.forEach { addListenerTo(it) } }
            .let { State.create(it) }
    }

    private fun <T : Navigator.Events> addListenerTo(navigator: Navigator<T>) {
        // Child navigators have a shorter lifetime than their parent navigators,
        // so it is not necessary to unregister the listener.
        // noinspection CheckResult
        navigator.addListener(this@CompositeStackNavigator as T)
    }

    /**
     * The list of [E] instances that have registered with this Navigator.
     *
     * @see addListener
     */
    protected val listeners: List<E> get() = _listeners

    private val _listeners = mutableListOf<E>()
    override fun addListener(listener: E): DisposableHandle {
        _listeners += listener

        if (state is State.Active) {
            scene?.let { listener.scene(it) }
        }

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
    fun push(navigator: Navigator<out Navigator.Events>) {
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
        state = state.pop()

        if (state is State.Destroyed) {
            _listeners.forEach { it.finished() }
        }
    }

    override fun onStart() {
        v("CompositeStackNavigator", "onStart")

        state = state.start()
    }

    override fun onStop() {
        v("CompositeStackNavigator", "onStop")
        state = state.stop()
    }

    override fun onDestroy() {
        v("CompositeStackNavigator", "onDestroy")
        state = state.destroy()
    }

    private var scene: Scene<out Container>? = null
    override fun scene(scene: Scene<out Container>) {
        this.scene = scene
        _listeners.onEach { it.scene(scene) }
    }

    override fun finished() {
        state = state.pop()
    }

    override fun onBackPressed(): Boolean {
        val result = state.let { state ->
            when (state) {
                is State.Inactive -> (state.navigator as? OnBackPressListener)?.onBackPressed() ?: false
                is State.Active -> (state.navigator as? OnBackPressListener)?.onBackPressed() ?: false
                is State.Destroyed -> false
            }
        }

        if (state is State.Destroyed) {
            _listeners.forEach { it.finished() }
        }

        return result
    }

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

    private sealed class State {

        abstract val navigators: List<Navigator<out Navigator.Events>>

        abstract fun start(): State
        abstract fun stop(): State
        abstract fun destroy(): State

        abstract fun push(navigator: Navigator<out Navigator.Events>): State
        abstract fun pop(): State

        companion object {

            fun create(initialStack: List<Navigator<out Navigator.Events>>): State {
                return Inactive(initialStack)
            }
        }

        class Inactive(override val navigators: List<Navigator<out Navigator.Events>>) : State() {

            init {
                check(navigators.isNotEmpty()) { "Stack may not be empty." }
            }

            val navigator: Navigator<out Navigator.Events> get() = navigators.last()

            override fun start(): State {
                navigators.last().onStart()
                return Active(navigators)
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                navigators.asReversed().forEach { it.onDestroy() }
                return Destroyed()
            }

            override fun push(navigator: Navigator<out Navigator.Events>): State {
                return Inactive(navigators + navigator)
            }

            override fun pop(): State {
                navigators.last().onDestroy()
                val newScenes = navigators.dropLast(1)

                return when {
                    newScenes.isEmpty() -> Destroyed()
                    else -> Inactive(newScenes)
                }
            }
        }

        class Active(override val navigators: List<Navigator<out Navigator.Events>>) : State() {

            init {
                check(navigators.isNotEmpty()) { "Stack may not be empty." }
            }

            val navigator: Navigator<out Navigator.Events> get() = navigators.last()

            override fun start(): State {
                return this
            }

            override fun stop(): State {
                navigators.last().onStop()
                return Inactive(navigators)
            }

            override fun destroy(): State {
                return stop().destroy()
            }

            override fun push(navigator: Navigator<out Navigator.Events>): State {
                navigators.last().onStop()
                navigator.onStart()
                return Active(navigators + navigator)
            }

            override fun pop(): State {
                val poppedScene = navigators.last()
                poppedScene.onStop()
                poppedScene.onDestroy()

                val newScenes = navigators.dropLast(1)

                return when {
                    newScenes.isEmpty() -> Destroyed()
                    else -> {
                        newScenes.last().onStart()
                        Active(newScenes)
                    }
                }
            }
        }

        class Destroyed : State() {

            override val navigators: List<Navigator<out Navigator.Events>> = emptyList()

            override fun start(): State {
                w("State", "Warning: Cannot start state after navigator is destroyed.")
                return this
            }

            override fun stop(): State {
                return this
            }

            override fun destroy(): State {
                return this
            }

            override fun push(navigator: Navigator<out Navigator.Events>): State {
                w("State", "Warning: Cannot push navigator after parent navigator is destroyed.")
                return this
            }

            override fun pop(): State {
                w("State", "Warning: Cannot pop scene after navigator is destroyed.")
                return this
            }
        }
    }
}
