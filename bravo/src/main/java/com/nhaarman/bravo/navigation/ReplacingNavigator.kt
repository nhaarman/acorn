package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.NavigatorState
import com.nhaarman.bravo.NavigatorState.Companion.navigatorState
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.internal.v
import com.nhaarman.bravo.internal.w
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.util.lazyVar

/**
 * A navigator class that can switch between [Scene]s, but has no 'back'
 * behavior.
 */
abstract class ReplacingNavigator(
    private val savedState: NavigatorState?
) : Navigator<Navigator.Events>, SaveableNavigator, OnBackPressListener {

    /**
     * Returns the Scene this Navigator should start with.
     *
     * Will only be called once in the lifetime of the Navigator, and zero times
     * if the Navigator is being restored from a saved state.
     */
    abstract fun initialScene(): Scene<out Container>

    /**
     * Instantiates the Scene for given [sceneClass] and [state].
     *
     * This method is usually invoked when the Navigator is being restored from
     * a saved state.
     *
     * @param sceneClass The class of the [Scene] to instantiate
     * @param state An optional saved state instance to restore the new Scene's
     *              state from
     */
    abstract fun instantiateScene(sceneClass: Class<*>, state: SceneState?): Scene<out Container>

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
     */
    fun replace(newScene: Scene<out Container>) {
        state = state.replaceWith(newScene)

        if (state is LifecycleState.Active) {
            listeners.forEach { it.scene(state.scene) }
        }
    }

    private var state by lazyVar {
        fun initialScene(): Scene<out Container> {
            val savedClass = savedState?.sceneClass ?: return this@ReplacingNavigator.initialScene()
            val savedState = savedState.sceneState

            return instantiateScene(savedClass, savedState)
        }

        LifecycleState.create(initialScene())
    }

    private val listeners = mutableListOf<Navigator.Events>()
    override fun addListener(listener: Navigator.Events): DisposableHandle {
        listeners += listener

        if (state is LifecycleState.Active) {
            listener.scene(state.scene)
        }

        return object : DisposableHandle {

            override fun isDisposed(): Boolean {
                return listener in listeners
            }

            override fun dispose() {
                listeners -= listener
            }
        }
    }

    override fun onStart() {
        v("ReplacingNavigator", "onStart")

        state = state.start()
        listeners.forEach { it.scene(state.scene) }
    }

    override fun onStop() {
        v("ReplacingNavigator", "onStop")
        state = state.stop()
    }

    override fun onDestroy() {
        v("ReplacingNavigator", "onDestroy")
        state = state.destroy()
    }

    override fun onBackPressed(): Boolean {
        v("ReplacingNavigator", "onBackPressed")
        state = state.stop().destroy()

        listeners.forEach { it.finished() }
        return true
    }

    override fun saveInstanceState(): NavigatorState {
        return navigatorState {
            it.sceneClass = state.scene.javaClass
            it.sceneState = (state.scene as? SaveableScene)?.saveInstanceState()
        }
    }

    private sealed class LifecycleState {

        abstract val scene: Scene<out Container>

        abstract fun start(): LifecycleState
        abstract fun stop(): LifecycleState
        abstract fun destroy(): LifecycleState

        abstract fun replaceWith(scene: Scene<out Container>): LifecycleState

        companion object {

            fun create(scene: Scene<out Container>): LifecycleState {
                return Inactive(scene)
            }
        }

        class Inactive(override val scene: Scene<out Container>) : LifecycleState() {

            override fun start(): LifecycleState {
                scene.onStart()
                return Active(scene)
            }

            override fun stop(): LifecycleState {
                return this
            }

            override fun destroy(): LifecycleState {
                scene.onDestroy()
                return Destroyed(scene)
            }

            override fun replaceWith(scene: Scene<out Container>): LifecycleState {
                return Inactive(scene)
            }
        }

        class Active(override val scene: Scene<out Container>) : LifecycleState() {

            override fun start(): LifecycleState {
                return this
            }

            override fun stop(): LifecycleState {
                scene.onStop()
                return Inactive(scene)
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
                return Active(scene)
            }
        }

        class Destroyed(override val scene: Scene<out Container>) : LifecycleState() {

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
        }
    }

    companion object {

        private var NavigatorState.sceneClass: Class<*>?
            get() = get<String>("scene:class")?.let { Class.forName(it) }
            set(value) {
                set("scene:class", value?.name)
            }

        private var NavigatorState.sceneState: SceneState?
            get() = get("scene:state")
            set(value) {
                set("scene:state", value)
            }
    }
}