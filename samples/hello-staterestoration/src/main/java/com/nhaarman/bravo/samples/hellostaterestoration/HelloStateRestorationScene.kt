package com.nhaarman.bravo.samples.hellostaterestoration

import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.presentation.BaseSaveableScene
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

/**
 * A [Scene] implementation that can have its state saved.
 *
 * This implementation extends the [BaseSaveableScene] class which provides
 * default behavior for saving the Scene and its [Container] state.
 */
class HelloStateRestorationScene private constructor(
    private val counter: Int,
    private val listener: Events,
    savedState: SceneState?
) : BaseSaveableScene<HelloStateRestorationContainer>(savedState) {

    override fun attach(v: HelloStateRestorationContainer) {
        super.attach(v)

        v.counterValue = counter
        v.onNextClicked { listener.nextRequested() }
    }

    override fun saveInstanceState(): SceneState {
        return super.saveInstanceState()
            .also { it.counter = counter }
    }

    interface Events {

        fun nextRequested()
    }

    companion object {

        /**
         * Creates a new instance for given counter, without any saved state.
         */
        fun create(counter: Int, listener: Events): HelloStateRestorationScene {
            return HelloStateRestorationScene(counter, listener, null)
        }

        /**
         * Creates a new instance from given saved instance state.
         * The counter value is retrieved from the saved state.
         */
        fun create(savedState: SceneState, listener: Events): HelloStateRestorationScene {
            val counter = savedState.counter!!
            return HelloStateRestorationScene(counter, listener, savedState)
        }

        private var SceneState.counter: Int?
            get() = get("counter")
            set(value) {
                this["counter"] = value
            }
    }
}