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

package com.nhaarman.acorn.samples.hellostaterestoration

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ProvidesView
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.BaseSavableScene
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get

/**
 * A [Scene] implementation that can have its state saved.
 *
 * This implementation extends the [BaseSavableScene] class which provides
 * default behavior for saving the Scene and its [Container] state.
 */
class HelloStateRestorationScene private constructor(
    private val counter: Int,
    private val listener: Events,
    savedState: SceneState?
) : BaseSavableScene<HelloStateRestorationContainer>(savedState), ProvidesView {

    override fun createViewController(parent: ViewGroup): ViewController {
        return HelloStateRestorationViewController(parent.inflate(R.layout.myscene))
    }

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
            get() = this["counter"]
            set(value) {
                this["counter"] = value
            }
    }
}