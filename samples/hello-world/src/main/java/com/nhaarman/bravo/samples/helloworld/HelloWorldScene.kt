package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.presentation.Scene

class HelloWorldScene : Scene<HelloWorldContainer> {

    override val key = HelloWorldScene.key

    override fun attach(v: HelloWorldContainer) {
        v.text = "Hello, world!"
    }

    companion object {

        val key = HelloWorldScene::class.java.name
    }
}