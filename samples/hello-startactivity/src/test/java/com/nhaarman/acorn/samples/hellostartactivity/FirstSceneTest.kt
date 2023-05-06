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

package com.nhaarman.acorn.samples.hellostartactivity

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FirstSceneTest {

    private val listener = mock<FirstScene.Events>()
    private val scene = FirstScene(listener)
    private val container = TestContainer()

    @Test
    fun `clicking button notifies listener`() {
        // Given
        scene.attach(container)

        // When
        container.clickSecondScene()

        // Then
        verify(listener).settingsRequested()
    }

    private class TestContainer : FirstSceneContainer {

        fun clickSecondScene() {
            listener?.invoke()
        }

        private var listener: (() -> Unit)? = null
        override fun onButtonClicked(f: () -> Unit) {
            listener = f
        }
    }
}
