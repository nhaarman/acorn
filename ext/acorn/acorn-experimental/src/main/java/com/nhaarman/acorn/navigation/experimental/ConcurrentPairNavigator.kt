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

package com.nhaarman.acorn.navigation.experimental

import androidx.annotation.CallSuper
import com.nhaarman.acorn.OnBackPressListener
import com.nhaarman.acorn.navigation.DisposableHandle
import com.nhaarman.acorn.navigation.Navigator
import com.nhaarman.acorn.navigation.SavableNavigator
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.navigation.experimental.internal.v
import com.nhaarman.acorn.navigation.experimental.internal.w
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.navigatorState
import com.nhaarman.acorn.util.lazyVar
import kotlin.reflect.KClass

/**
 * An abstract [Navigator] class that supports a second, overlapping [Scene].
 *
 * This Navigator supports two states: one where only the initial Scene is
 * active, and one where both the initial Scene and an additional Scene are
 * active. When adding the second Scene using [push], both the initial and the
 * additional Scene will be in their 'started' state. This can be useful when
 * showing a sophisticated modal kind of overlay.
 *
 * When the additional Scene is pushed, this Navigator wraps both the initial
 * Scene and the second Scene in a [CombinedScene] and notifies interested
 * [Navigator.Events] of this CombinedScene.
 *
 * This Navigator implements [SavableNavigator] and thus can have its state
 * saved and restored when necessary.
 *
 * @param savedState An optional instance that contains the saved state as
 * returned by this class's [saveInstanceState] method.
 */
@ExperimentalConcurrentPairNavigator
abstract class ConcurrentPairNavigator(
    private val savedState: NavigatorState?
) : Navigator, SavableNavigator, OnBackPressListener {

    /**
     * Creates the initial [Scene] for this ConcurrentPairNavigator.
     */
    protected abstract fun createInitialScene(): Scene<out Container>

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

    private var state: State by lazyVar {

        @Suppress("UNCHECKED_CAST")
        fun baseScene(savedState: NavigatorState): Scene<out Container>? {
            val className = savedState.get<String>("base_class") ?: return null
            val baseSceneClass = Class.forName(className).kotlin as? KClass<out Scene<*>> ?: return null
            val baseSceneState: SceneState = savedState["base_state"] ?: return null
            return instantiateScene(baseSceneClass, baseSceneState)
        }

        @Suppress("UNCHECKED_CAST")
        fun secondScene(savedState: NavigatorState): Scene<out Container>? {
            val className = savedState.get<String>("second_class") ?: return null
            val secondSceneClass = Class.forName(className).kotlin as? KClass<out Scene<*>> ?: return null
            val secondSceneState: SceneState? = savedState["second_state"] ?: return null
            return instantiateScene(secondSceneClass, secondSceneState)
        }

        fun initialScenes(): Scenes {
            if (savedState == null) return Scenes(
                createInitialScene()
            )

            val baseScene = baseScene(savedState) ?: return Scenes(
                createInitialScene()
            )
            val secondScene = secondScene(savedState)

            return Scenes(baseScene, secondScene)
        }

        State.create(initialScenes())
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
     * Sets given [Scene] as the second Scene in this ConcurrentPairNavigator.
     *
     * If this Navigator is currently active and there is already a second
     * Scene active, this second Scene will be stopped and destroyed. Given
     * Scene will receive a call to [Scene.onStart]. The initial Scene will
     * receive no lifecycle methods.
     *
     * If this Navigator is currently inactive and there is already a second
     * Scene active, this second Scene will be destroyed. No further lifecycle
     * methods will be called. Starting this Navigator will start both the
     * initial Scene and given second Scene.
     *
     * Calling this method when this Navigator has been destroyed will have no
     * effect.
     *
     * @param scene The [Scene] instance to push.
     */
    fun push(scene: Scene<out Container>) {
        v("ConcurrentPairNavigator", "push $scene")
        execute(state.push(scene, TransitionData.forwards))
    }

    /**
     * Removes the second [Scene] if there is any.
     *
     * If there is no second Scene, nothing will happen.
     *
     * If this Navigator is currently active and there is a second Scene, this
     * second Scene will be stopped and destroyed.
     *
     * If this Navigator is currently inactive and there is a second Scene, this
     * second Scene will be destroyed.
     *
     * Calling this method when the receiving Navigator has been destroyed will
     * have no effect.
     */
    fun pop() {
        execute(state.pop())
    }

    /**
     * Finishes this Navigator.
     *
     * If this Navigator is currently active, any active Scenes will go through
     * their destroying lifecycles calling [Scene.onStop] and [Scene.onDestroy].
     *
     * If this Navigator is currently not active, any active Scenes will only
     * have their [Scene.onDestroy] method called.
     *
     * Calling this method when the Navigator has been destroyed will have no
     * effect.
     */
    fun finish() {
        execute(state.finish())
    }

    @CallSuper
    override fun onStart() {
        v("ConcurrentPairNavigator", "onStart")
        execute(state.start())
    }

    @CallSuper
    override fun onStop() {
        v("ConcurrentPairNavigator", "onStop")
        execute(state.stop())
    }

    @CallSuper
    override fun onDestroy() {
        v("ConcurrentPairNavigator", "onDestroy")
        execute(state.destroy())
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        if (isDestroyed()) return false

        execute(state.onBackPressed())
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
    override fun saveInstanceState(): NavigatorState {
        return navigatorState {
            it["base_class"] = state.scenes.baseScene::class.java.name
            it["base_state"] = (state.scenes.baseScene as? SavableScene)?.saveInstanceState()

            it["second_class"] = state.scenes.secondScene?.let { it::class.java.name }
            it["second_state"] = (state.scenes.secondScene as? SavableScene)?.saveInstanceState()
        }
    }

    private sealed class State {

        abstract val scenes: Scenes
        abstract val listeners: List<Navigator.Events>

        abstract fun addListener(listener: Navigator.Events)
        abstract fun removeListener(listener: Navigator.Events)

        abstract fun start(): StateTransition
        abstract fun stop(): StateTransition
        abstract fun destroy(): StateTransition

        abstract fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition
        abstract fun pop(): StateTransition
        abstract fun onBackPressed(): StateTransition

        abstract fun finish(): StateTransition

        companion object {

            fun create(initialScenes: Scenes): State {
                return Inactive(
                    initialScenes,
                    emptyList()
                )
            }
        }

        class Inactive(
            override val scenes: Scenes,
            override var listeners: List<Navigator.Events>
        ) : State() {

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): StateTransition {
                return StateTransition(
                    Active(
                        scenes,
                        listeners
                    )
                ) {
                    scenes.baseScene.onStart()
                    scenes.secondScene?.onStart()
                    listeners.forEach { it.scene(scenes.scene(), null) }
                }
            }

            override fun stop(): StateTransition {
                return StateTransition(
                    Inactive(
                        scenes,
                        listeners
                    )
                )
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    scenes.secondScene?.onDestroy()
                    scenes.baseScene.onDestroy()
                }
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                return StateTransition(
                    Inactive(
                        Scenes(scenes.baseScene, scene),
                        listeners
                    )
                ) {
                    scenes.secondScene?.onDestroy()
                }
            }

            override fun pop(): StateTransition {
                return StateTransition(this) {
                    scenes.secondScene?.onDestroy()
                }
            }

            override fun onBackPressed(): StateTransition {
                val poppedScene = scenes.secondScene
                if (poppedScene == null) {
                    return StateTransition(this) {
                        scenes.baseScene.onDestroy()
                        listeners.forEach { it.finished() }
                    }
                }

                return StateTransition(
                    Inactive(
                        Scenes(scenes.baseScene),
                        listeners
                    )
                ) {
                    poppedScene.onDestroy()
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    scenes.secondScene?.onDestroy()
                    scenes.baseScene.onDestroy()
                    listeners.forEach { it.finished() }
                }
            }
        }

        class Active(
            override val scenes: Scenes,
            override var listeners: List<Navigator.Events>
        ) : State() {

            override fun addListener(listener: Navigator.Events) {
                listeners += listener
                listener.scene(scenes.scene(), null)
            }

            override fun removeListener(listener: Navigator.Events) {
                listeners -= listener
            }

            override fun start(): StateTransition {
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(
                    Inactive(
                        scenes,
                        listeners
                    )
                ) {
                    scenes.secondScene?.onStop()
                    scenes.baseScene.onStop()
                }
            }

            override fun destroy(): StateTransition {
                return StateTransition(Destroyed()) {
                    scenes.secondScene?.onStop()
                    scenes.baseScene.onStop()

                    scenes.secondScene?.onDestroy()
                    scenes.baseScene.onDestroy()
                }
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                val newScenes =
                    Scenes(scenes.baseScene, scene)
                return StateTransition(
                    Active(
                        newScenes,
                        listeners
                    )
                ) {
                    scenes.secondScene?.onStop()
                    scenes.secondScene?.onDestroy()

                    scene.onStart()
                    listeners.forEach { it.scene(newScenes.scene(), data) }
                }
            }

            override fun pop(): StateTransition {
                val poppedScene = scenes.secondScene
                if (poppedScene == null) {
                    return StateTransition(this)
                }

                return StateTransition(
                    Active(
                        Scenes(scenes.baseScene),
                        listeners
                    )
                ) {
                    poppedScene.onStop()
                    poppedScene.onDestroy()
                    listeners.forEach { it.scene(scenes.baseScene, TransitionData.backwards) }
                }
            }

            override fun onBackPressed(): StateTransition {
                val poppedScene = scenes.secondScene
                if (poppedScene == null) {
                    return StateTransition(Destroyed()) {
                        scenes.baseScene.onStop()
                        scenes.baseScene.onDestroy()
                        listeners.forEach { it.finished() }
                    }
                }

                return StateTransition(
                    Active(
                        Scenes(scenes.baseScene),
                        listeners
                    )
                ) {
                    poppedScene.onStop()
                    poppedScene.onDestroy()

                    listeners.forEach { it.scene(scenes.baseScene) }
                }
            }

            override fun finish(): StateTransition {
                return StateTransition(Destroyed()) {
                    scenes.secondScene?.onStop()
                    scenes.baseScene.onStop()

                    scenes.secondScene?.onDestroy()
                    scenes.baseScene.onDestroy()

                    listeners.forEach { it.finished() }
                }
            }
        }

        class Destroyed : State() {

            override val scenes: Scenes get() = error("")
            override val listeners = emptyList<Navigator.Events>()

            override fun addListener(listener: Navigator.Events) {
            }

            override fun removeListener(listener: Navigator.Events) {
            }

            override fun start(): StateTransition {
                w("ConcurrentPairNavigator.State", "Warning: Cannot start state after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun stop(): StateTransition {
                return StateTransition(this)
            }

            override fun destroy(): StateTransition {
                return StateTransition(this)
            }

            override fun push(scene: Scene<out Container>, data: TransitionData?): StateTransition {
                w("ConcurrentPairNavigator.State", "Warning: Cannot push scene after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun pop(): StateTransition {
                w("ConcurrentPairNavigator.State", "Warning: Cannot pop scene after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun onBackPressed(): StateTransition {
                w("ConcurrentPairNavigator.State", "Warning: Cannot handle onBackPressed after navigator is destroyed.")
                return StateTransition(this)
            }

            override fun finish(): StateTransition {
                w("ConcurrentPairNavigator.State", "Warning: Cannot finish navigator after navigator is destroyed.")
                return StateTransition(this)
            }
        }
    }

    private class Scenes(
        val baseScene: Scene<out Container>,
        val secondScene: Scene<out Container>? = null
    ) {

        fun scene(): Scene<*> {
            if (secondScene != null) {
                return CombinedScene(baseScene, secondScene)
            }

            return baseScene
        }
    }

    private class StateTransition(
        val newState: State,
        val action: (() -> Unit)? = null
    )
}
