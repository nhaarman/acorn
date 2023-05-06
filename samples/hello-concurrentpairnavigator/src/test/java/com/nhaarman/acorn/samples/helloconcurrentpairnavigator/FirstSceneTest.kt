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

package com.nhaarman.acorn.samples.helloconcurrentpairnavigator

import com.nhaarman.expect.expect
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.concurrent.TimeUnit

class FirstSceneTest {

    private val scheduler = TestScheduler()
    private val listener = mock<FirstScene.Events>()
    private val scene = FirstScene(listener, scheduler)
    private val container = TestContainer()

    @Test
    fun `clicking button notifies listener`() {
        // Given
        scene.attach(container)

        // When
        container.clickAction()

        // Then
        verify(listener).actionClicked()
    }

    @Test
    fun `count is applied to the container when attached later`() {
        // Given
        scene.onStart()

        // When
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        scene.attach(container)

        // Then
        expect(container.count).toBe(50)
    }

    @Test
    fun `count is applied to the container after attaching`() {
        // Given
        scene.onStart()

        // When
        scene.attach(container)
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)

        // Then
        expect(container.count).toBe(50)
    }

    private class TestContainer : FirstSceneContainer {

        override var count: Long = 0

        fun clickAction() {
            listener?.invoke()
        }

        private var listener: (() -> Unit)? = null
        override fun onActionClicked(f: () -> Unit) {
            listener = f
        }
    }
}
