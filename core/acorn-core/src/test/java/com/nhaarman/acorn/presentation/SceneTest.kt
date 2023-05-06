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

package com.nhaarman.acorn.presentation

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy

/**
 * A set of tests to specify default Scene behavior.
 */
class SceneTest {

    val scene = spy(TestScene())

    @Test
    fun `default scene key`() {
        // Given
        val scene = TestScene()

        // Then
        expect(scene.key).toBe(SceneKey.from(TestScene::class))
    }

    @Test
    fun `calling onStart does nothing`() {
        // When
        scene.onStart()

        // Then
        scene.inOrder {
            verify().onStart()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling attach does nothing`() {
        // When
        scene.attach(mock())

        // Then
        scene.inOrder {
            verify().attach(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling detach does nothing`() {
        // When
        scene.detach(mock())

        // Then
        scene.inOrder {
            verify().detach(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling onStop does nothing`() {
        // When
        scene.onStop()

        // Then
        scene.inOrder {
            verify().onStop()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `calling onDestroy does nothing`() {
        // When
        scene.onDestroy()

        // Then
        scene.inOrder {
            verify().onDestroy()
            verifyNoMoreInteractions()
        }
    }

    open class TestScene : Scene<Container>
}
