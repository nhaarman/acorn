/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.samples.hellostaterestoration

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.ProvidesView
import com.nhaarman.acorn.android.presentation.ViewController
import com.nhaarman.acorn.android.util.inflate
import com.nhaarman.acorn.presentation.BaseSaveableScene
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get

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
) : BaseSaveableScene<HelloStateRestorationContainer>(savedState), ProvidesView {

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