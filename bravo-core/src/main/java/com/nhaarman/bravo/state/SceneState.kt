package com.nhaarman.bravo.state

interface SceneState : SavedState {

    operator fun set(key: String, value: ContainerState?)
}