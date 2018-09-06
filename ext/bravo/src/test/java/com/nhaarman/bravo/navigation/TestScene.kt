/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.navigation.TestScene.State.Created
import com.nhaarman.bravo.navigation.TestScene.State.Destroyed
import com.nhaarman.bravo.navigation.TestScene.State.Started
import com.nhaarman.bravo.navigation.TestScene.State.Stopped
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.presentation.Scene
import com.nhaarman.bravo.state.SceneState
import com.nhaarman.bravo.state.get
import com.nhaarman.bravo.state.sceneState

open class TestScene(var foo: Int) : Scene<Container>, SaveableScene {

    var state = Created

    override fun onStart() {
        state = Started
    }

    override fun onStop() {
        state = Stopped
    }

    override fun onDestroy() {
        state = Destroyed
    }

    override fun toString(): String {
        return "TestScene($foo)"
    }

    enum class State {
        Created,
        Started,
        Stopped,
        Destroyed
    }

    override fun saveInstanceState(): SceneState {
        return sceneState { it["foo"] = foo }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestScene

        if (foo != other.foo) return false

        return true
    }

    override fun hashCode(): Int {
        return foo
    }

    companion object {

        fun create(state: SceneState?): TestScene {
            return TestScene(
                foo = state?.get("foo") ?: 0
            )
        }
    }
}