package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.presentation.Scene

class HelloWorldScene : Scene<HelloWorldContainer> {

    override fun attach(v: HelloWorldContainer) {
        v.text = "Hello, world!"
    }
}