package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.internal.v
import com.nhaarman.bravo.internal.w
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.get
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
            .let { LifecycleState.create(it) }
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
    @Suppress("UNCHECKED_CAST")
    protected val listeners: List<E>
        get() = state.listeners as List<E>

    override fun addListener(listener: E): DisposableHandle {
        state.addListener(listener)

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in listeners
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

    override fun scene(scene: Scene<out Container>, data: TransitionData?) {
        v("CompositeStackNavigator", "Scene change: $scene, $data")
        state.scene(scene, data)
    }

    override fun finished() {
        state = state.pop()
    }

    override fun onBackPressed(): Boolean {
        v("CompositeStackNavigator", "onBackPressed")
        return state.onBackPressed()
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

    override fun isDestroyed(): Boolean {
        return state is LifecycleState.Destroyed
    }

    private sealed class LifecycleState {

        abstract val navigators: List<Navigator<out Navigator.Events>>
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): LifecycleState
        abstract fun stop(): LifecycleState
        abstract fun destroy(): LifecycleState

        abstract fun scene(scene: Scene<out Container>, data: TransitionData?)

        abstract fun push(navigator: Navigator<out Navigator.Events>): LifecycleState
        abstract fun pop(): LifecycleState

        abstract fun onBackPressed(): Boolean

        companion object {

            fun create(initialStack: List<Navigator<out Navigator.Events>>): LifecycleState {
                return Inactive(initialStack, emptyList(), null)
            }
        }

        class Inactive(
            override val navigators: List<Navigator<out Navigator.Events>>,
            override var listeners: List<Navigator.Events>,
            private var activeScene: Scene<out Container>?
        ) : LifecycleState() {

            init {
                check(navigators.isNotEmpty()) { "Stack may not be empty." }
            }

            private val navigator: Navigator<out Navigator.Events> get() = navigators.last()

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

            override fun push(navigator: Navigator<out Navigator.Events>): LifecycleState {
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

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Active(
            override var navigators: List<Navigator<out Navigator.Events>>,
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

            val navigator: Navigator<out Navigator.Events> get() = navigators.last()

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
            override fun push(navigator: Navigator<out Navigator.Events>): LifecycleState {
                backward = false

                navigators.last().onStop()
                navigator.onStart()
                navigators += navigator
                return this
            }

            override fun pop(): LifecycleState {
                backward = true

                val poppedScene = navigators.last()
                poppedScene.onStop()
                poppedScene.onDestroy()

                val newScenes = navigators.dropLast(1)

                return when {
                    newScenes.isEmpty() -> {
                        listeners.forEach { it.finished() }
                        Destroyed()
                    }
                    else -> {
                        newScenes.last().onStart()
                        navigators = newScenes
                        this
                    }
                }
            }

            override fun onBackPressed(): Boolean {
                return (navigator as? OnBackPressListener)?.onBackPressed() ?: false
            }
        }

        class Destroyed : LifecycleState() {

            override val navigators: List<Navigator<out Navigator.Events>> = emptyList()
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

            override fun push(navigator: Navigator<out Navigator.Events>): LifecycleState {
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

            override fun onBackPressed(): Boolean {
                return false
            }
        }
    }
}
