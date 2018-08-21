package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.BravoBundle.Companion.bundle
import com.nhaarman.bravo.OnBackPressListener
import com.nhaarman.bravo.internal.v
import com.nhaarman.bravo.internal.w
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.StateSaveable
import com.nhaarman.bravo.util.lazyVar
import io.reactivex.disposables.Disposable

/**
 * A navigator class that can switch between [Scene]s, but has no 'back'
 * behavior.
 */
abstract class ReplacingNavigator(
    private val savedState: BravoBundle?
) : Navigator<Navigator.Events>, StateSaveable, OnBackPressListener {

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
    abstract fun instantiateScene(sceneClass: Class<*>, state: BravoBundle?): Scene<out Container>

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

        if (state is SceneState.Active) {
            listeners.forEach { it.scene(state.scene) }
        }
    }

    private var state by lazyVar {
        fun initialScene(): Scene<out Container> {
            val savedClass = savedState?.sceneClass ?: return this@ReplacingNavigator.initialScene()
            val savedState = savedState.sceneState

            return instantiateScene(savedClass, savedState)
        }

        SceneState.create(initialScene())
    }

    private val listeners = mutableListOf<Navigator.Events>()
    override fun addListener(listener: Navigator.Events): Disposable {
        listeners += listener

        if (state is SceneState.Active) {
            listener.scene(state.scene)
        }

        return object : Disposable {

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

    override fun saveInstanceState(): BravoBundle {
        return bundle {
            it.sceneClass = state.scene.javaClass
            it.sceneState = (state.scene as? StateSaveable)?.saveInstanceState()
        }
    }

    private sealed class SceneState {

        abstract val scene: Scene<out Container>

        abstract fun start(): SceneState
        abstract fun stop(): SceneState
        abstract fun destroy(): SceneState

        abstract fun replaceWith(scene: Scene<out Container>): SceneState

        companion object {

            fun create(scene: Scene<out Container>): SceneState {
                return Inactive(scene)
            }
        }

        class Inactive(override val scene: Scene<out Container>) : SceneState() {

            override fun start(): SceneState {
                scene.onStart()
                return Active(scene)
            }

            override fun stop(): SceneState {
                return this
            }

            override fun destroy(): SceneState {
                scene.onDestroy()
                return Destroyed(scene)
            }

            override fun replaceWith(scene: Scene<out Container>): SceneState {
                return Inactive(scene)
            }
        }

        class Active(override val scene: Scene<out Container>) : SceneState() {

            override fun start(): SceneState {
                return this
            }

            override fun stop(): SceneState {
                scene.onStop()
                return Inactive(scene)
            }

            override fun destroy(): SceneState {
                scene.onStop()
                scene.onDestroy()
                return Destroyed(scene)
            }

            override fun replaceWith(scene: Scene<out Container>): SceneState {
                this.scene.onStop()
                this.scene.onDestroy()
                scene.onStart()
                return Active(scene)
            }
        }

        class Destroyed(override val scene: Scene<out Container>) : SceneState() {

            override fun start(): SceneState {
                w("SceneState", "Warning: Cannot start state after it is destroyed.")
                return this
            }

            override fun stop(): SceneState {
                return this
            }

            override fun destroy(): SceneState {
                return this
            }

            override fun replaceWith(scene: Scene<out Container>): SceneState {
                w("SceneState", "Warning: Cannot replace scene after state is destroyed.")
                return this
            }
        }
    }

    companion object {

        private var BravoBundle.sceneClass: Class<*>?
            get() = get<String>("scene:class")?.let { Class.forName(it) }
            set(value) {
                set("scene:class", value?.name)
            }

        private var BravoBundle.sceneState: BravoBundle?
            get() = get("scene:state")
            set(value) {
                set("scene:state", value)
            }
    }
}