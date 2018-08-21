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
 * A simple [Navigator] that only hosts a single [Scene].
 *
 * This Navigator implements [StateSaveable] and thus can have its state saved
 * and restored when necessary.
 *
 * @param savedState An optional instance that contains saved state as returned
 *                   by [saveInstanceState].
 */
abstract class SingleSceneNavigator<Events : Navigator.Events>(
    private val savedState: BravoBundle?
) : Navigator<Events>, StateSaveable, OnBackPressListener {

    /**
     * Creates the [Scene] instance to host.
     *
     * @param state An optional saved state instance to restore the Scene's
     *              state from.
     */
    abstract fun createScene(state: BravoBundle?): Scene<out Container>

    private val scene by lazy { createScene(savedState?.sceneState) }

    private var state by lazyVar { SceneState.create(scene) }

    protected val listeners = mutableListOf<Events>()
    override fun addListener(listener: Events): Disposable {
        listeners += listener

        if (state is SceneState.Active) {
            listener.scene(scene)
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
        v("SingleSceneNavigator", "onStart")

        state = state.start()
        listeners.forEach { it.scene(scene) }
    }

    override fun onStop() {
        v("SingleSceneNavigator", "onStop")
        state = state.stop()
    }

    override fun onDestroy() {
        v("SingleSceneNavigator", "onDestroy")
        state = state.destroy()
    }

    override fun onBackPressed(): Boolean {
        v("SingleSceneNavigator", "onBackPressed")
        state = state.stop().destroy()

        listeners.forEach { it.finished() }
        return true
    }

    override fun saveInstanceState(): BravoBundle {
        return bundle {
            it.sceneState = (scene as? StateSaveable)?.saveInstanceState()
        }
    }

    private sealed class SceneState {

        abstract fun start(): SceneState
        abstract fun stop(): SceneState
        abstract fun destroy(): SceneState

        companion object {

            fun create(scene: Scene<out Container>): SceneState {
                return Inactive(scene)
            }
        }

        class Inactive(val scene: Scene<out Container>) : SceneState() {

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
        }

        class Active(val scene: Scene<out Container>) : SceneState() {

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
        }

        class Destroyed(val scene: Scene<out Container>) : SceneState() {

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
        }
    }

    companion object {

        private var BravoBundle.sceneState: BravoBundle?
            get() = get("scene:state")
            set(value) {
                set("scene:state", value)
            }
    }
}