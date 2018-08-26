package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.presentation.SaveableScene
import com.nhaarman.bravo.presentation.Scene

/**
 * A simple "Hello, World!" [Scene] implementation.
 *
 * This Scene does not handle any state restoration, since there is no state
 * worth saving.
 * In cases where state _is_ worth saving, your Scene should generally implement
 * [SaveableScene].
 */
class HelloWorldScene : Scene<HelloWorldContainer> {

    override fun attach(v: HelloWorldContainer) {
        v.text = "Hello, world!"
    }
}