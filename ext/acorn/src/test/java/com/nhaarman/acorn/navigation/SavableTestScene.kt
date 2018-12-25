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