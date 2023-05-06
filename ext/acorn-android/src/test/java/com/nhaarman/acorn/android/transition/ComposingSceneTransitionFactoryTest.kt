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

package com.nhaarman.acorn.android.transition

import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.android.util.TestTransition
import com.nhaarman.acorn.navigation.TransitionData
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ComposingSceneTransitionFactoryTest {

    private val scene1 = TestScene(SceneKey("1"))
    private val scene2 = TestScene(SceneKey("2"))
    private val scene3 = TestScene(SceneKey("3"))

    private val transition1 = TestTransition()
    private val transition2 = TestTransition()

    private val transitionFactory12 = TestSceneTransitionFactory(scene1, scene2, transition1)
    private val transitionFactory23 = TestSceneTransitionFactory(scene2, scene3, transition2)

    @Test
    fun `no sources results in false for supports call`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(emptyList())

        // When
        val result = factory.supports(scene1, scene2, null)

        // Then
        expect(result).toBe(false)
    }

    @Test
    fun `no sources results in exception for transition call`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(emptyList())

        // Expect
        expectErrorWithMessage("Could not create transition") on {
            // When
            factory.transitionFor(scene1, scene2, null)
        }
    }

    @Test
    fun `single source with matching transition supports true`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(
            transitionFactory12,
        )

        // When
        val result = factory.supports(scene1, scene2, null)

        // Then
        expect(result).toBe(true)
    }

    @Test
    fun `single source with matching transition results in proper transition`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(
            transitionFactory12,
        )

        // When
        val result = factory.transitionFor(scene1, scene2, null)

        // Then
        expect(result).toBe(transition1)
    }

    @Test
    fun `single source with non matching transition supports false`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(
            transitionFactory12,
        )

        // When
        val result = factory.supports(scene1, scene3, null)

        // Then
        expect(result).toBe(false)
    }

    @Test
    fun `single source with non matching transition results in exception`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(
            transitionFactory12,
        )

        // Expect
        expectErrorWithMessage("Could not create transition") on {
            // When
            factory.transitionFor(scene1, scene3, null)
        }
    }

    @Test
    fun `two sources with matching transition in second supports true`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(
            transitionFactory12,
            transitionFactory23,
        )

        // When
        val result = factory.supports(scene2, scene3, null)

        // Then
        expect(result).toBe(true)
    }

    @Test
    fun `single source with matching transition in second results in proper transition`() {
        // Given
        val factory = ComposingSceneTransitionFactory.from(
            transitionFactory12,
            transitionFactory23,
        )

        // When
        val result = factory.transitionFor(scene2, scene3, null)

        // Then
        expect(result).toBe(transition2)
    }

    @Nested
    inner class `issue 139` {

        @Test
        fun `a binding factory in a composing factory does not block second factory`() {
            // Given
            val factory = ComposingSceneTransitionFactory.from(
                sceneTransitionFactory { },
                transitionFactory12,
            )

            // When
            val result = factory.transitionFor(scene1, scene2, null)

            // Then
            expect(result).toBe(transition1)
        }
    }

    private class TestSceneTransitionFactory(
        private val fromScene: Scene<*>,
        private val toScene: Scene<*>,
        private val transition: SceneTransition,
    ) : SceneTransitionFactory {

        override fun supports(previousScene: Scene<*>, newScene: Scene<*>, data: TransitionData?): Boolean {
            return previousScene == fromScene && newScene == toScene
        }

        override fun transitionFor(
            previousScene: Scene<*>,
            newScene: Scene<*>,
            data: TransitionData?,
        ): SceneTransition {
            return transition
        }
    }
}
