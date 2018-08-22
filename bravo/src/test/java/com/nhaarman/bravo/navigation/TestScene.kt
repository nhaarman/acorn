package com.nhaarman.bravo.navigation

import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.SceneState
import com.nhaarman.bravo.SceneState.Companion.sceneState
import com.nhaarman.bravo.navigation.TestScene.State.Created
import com.nhaarman.bravo.navigation.TestScene.State.Destroyed
import com.nhaarman.bravo.navigation.TestScene.State.Started
import com.nhaarman.bravo.navigation.TestScene.State.Stopped
import com.nhaarman.bravo.presentation.Container
import com.nhaarman.bravo.presentation.Scene

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