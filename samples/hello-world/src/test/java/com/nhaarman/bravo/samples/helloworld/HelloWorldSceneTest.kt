package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class HelloWorldSceneTest {

    private val scene = HelloWorldScene()

    @Test
    fun `attaching container shows "Hello, world!" text`() {
        /* Given */
        val container = TestContainer()

        /* When */
        scene.attach(container)

        /* Then */
        expect(container.text).toBe("Hello, world!")
    }

    private class TestContainer : HelloWorldContainer {

        override var text: String = ""
    }
}