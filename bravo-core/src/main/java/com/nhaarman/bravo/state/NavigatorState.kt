package com.nhaarman.bravo.state

interface NavigatorState : SavedState {

    operator fun set(key: String, value: SceneState?)

    operator fun set(key: String, value: NavigatorState?)
}