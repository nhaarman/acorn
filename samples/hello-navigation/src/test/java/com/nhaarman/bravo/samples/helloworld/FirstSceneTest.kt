package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.bravo.samples.hellonavigation.FirstScene
import com.nhaarman.bravo.samples.hellonavigation.FirstSceneContainer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

class FirstSceneTest {

    private val listener = mock<FirstScene.Events>()
    private val scene = FirstScene(listener)
    private val container = TestContainer()

    @Test
    fun `clicking button notifies listener`() {
        /* Given */
        scene.attach(container)

        /* When */
        container.clickSecondScene()

        /* Then */
        verify(listener).secondSceneRequested()
    }

    private class TestContainer : FirstSceneContainer {

        fun clickSecondScene() {
            listener?.invoke()
        }

        private var listener: (() -> Unit)? = null
        override fun onSecondSceneClicked(f: () -> Unit) {
            listener = f
        }
    }
}