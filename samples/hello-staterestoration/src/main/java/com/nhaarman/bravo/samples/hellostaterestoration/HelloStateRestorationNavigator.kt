package com.nhaarman.bravo.samples.hellostaterestoration

import com.nhaarman.bravo.state.NavigatorState
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.navigation.Navigator
import com.nhaarman.bravo.navigation.StackNavigator
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A [Navigator] implementation that can be saved and restored.
 *
 * This implementation extends the [StackNavigator] which implements default
 * behavior for saving the state of this Navigator and its Scene.
 */
class HelloStateRestorationNavigator private constructor(
    private var counter: Int,
    savedState: NavigatorState?
) : StackNavigator<Navigator.Events>(savedState),
    HelloStateRestorationScene.Events {

    override fun initialStack(): List<Scene<out Container>> {
        return listOf(HelloStateRestorationScene.create(0, this))
    }

    override fun nextRequested() {
        counter++
        push(HelloStateRestorationScene.create(counter, this))
    }

    override fun onBackPressed(): Boolean {
        counter--
        return super.onBackPressed()
    }

    /**
     * Instantiates a [Scene] instance for given [sceneClass] and [state].
     *
     * This function is called when restoring the Navigator from a saved state.
     */
    override fun instantiateScene(sceneClass: Class<Scene<*>>, state: SceneState?): Scene<out Container> {
        return when (sceneClass) {
            HelloStateRestorationScene::class.java -> HelloStateRestorationScene.create(state!!, this)
            else -> error("Unknown Scene class: $sceneClass")
        }
    }

    /**
     * Overrides the default instance state saving behavior to add the counter
     * value.
     */
    override fun saveInstanceState(): NavigatorState {
        return super.saveInstanceState()
            .also { it.counter = counter }
    }

    companion object {

        /**
         * Creates a new Navigator instance for given optional saved state.
         */
        fun create(savedState: NavigatorState?): HelloStateRestorationNavigator {
            val counter = savedState?.counter ?: 0
            return HelloStateRestorationNavigator(counter, savedState)
        }

        private var NavigatorState.counter: Int?
            get() = get("counter")
            set(value) {
                this["counter"] = value
            }
    }
}