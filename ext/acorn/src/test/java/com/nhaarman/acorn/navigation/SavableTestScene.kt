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

package com.nhaarman.acorn.navigation

import com.nhaarman.acorn.navigation.SavableTestScene.State.Created
import com.nhaarman.acorn.navigation.SavableTestScene.State.Destroyed
import com.nhaarman.acorn.navigation.SavableTestScene.State.Started
import com.nhaarman.acorn.navigation.SavableTestScene.State.Stopped
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.SavableScene
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.state.SceneState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.sceneState

open class SavableTestScene(var foo: Int) : Scene<Container>, SavableScene {

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
        return "SavableTestScene($foo)"
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

        other as SavableTestScene

        if (foo != other.foo) return false

        return true
    }

    override fun hashCode(): Int {
        return foo
    }

    companion object {

        fun create(state: SceneState?): SavableTestScene {
            return SavableTestScene(
                foo = state?.get("foo") ?: 0
            )
        }
    }
}