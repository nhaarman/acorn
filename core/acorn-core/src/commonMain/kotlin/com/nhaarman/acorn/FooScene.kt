package com.nhaarman.acorn

import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey

class FooScene : Scene<Container> {

    override val key: SceneKey
        get() = SceneKey("foo2")

    override fun onStart() {
        println("foo")
    }
}

fun main(args: Array<String>) {
    FooScene().onStart()
}
