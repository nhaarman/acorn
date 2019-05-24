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

package com.nhaarman.acorn.android.transition.internal

import com.nhaarman.acorn.android.transition.SceneTransition
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.presentation.SceneKey
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class BindingSceneTransitionFactoryTest {

    private val scene1 = TestScene(SceneKey("1"))
    private val scene2 = TestScene(SceneKey("2"))
    private val scene3 = TestScene(SceneKey("3"))

    private val transition1 = mock<SceneTransition>()

    @Test
    fun `factory without bindings supports no transition`() {
        /* Given */
        val factory = BindingSceneTransitionFactory(bindings = emptySequence())

        /* When */
        val result = factory.supports(scene1, scene2, null)

        /* Then */
        expect(result).toBe(false)
    }

    @Test
    fun `requesting transition without bindings throws error`() {
        /* Given */
        val factory = BindingSceneTransitionFactory(bindings = emptySequence())

        /* Expect */
        expectErrorWithMessage("Could not create transition") on {

            /* When */
            factory.transitionFor(mock(), mock(), null)
        }
    }

    @Test
    fun `factory with single binding supports that transition`() {
        /* Given */
        val factory = BindingSceneTransitionFactory(
            bindings = sequenceOf(
                KeyBinding(
                    scene1.key,
                    scene2.key,
                    transition1
                )
            )
        )

        /* When */
        val result = factory.supports(scene1, scene2, null)

        /* Then */
        expect(result).toBe(true)
    }

    @Test
    fun `factory with single binding returns proper transition`() {
        /* Given */
        val factory = BindingSceneTransitionFactory(
            bindings = sequenceOf(
                KeyBinding(
                    scene1.key,
                    scene2.key,
                    transition1
                )
            )
        )

        /* When */
        val result = factory.transitionFor(scene1, scene2, null)

        /* Then */
        expect(result).toBe(transition1)
    }

    @Test
    fun `factory with single binding doesn't support other transition`() {
        /* Given */
        val factory = BindingSceneTransitionFactory(
            bindings = sequenceOf(
                KeyBinding(
                    scene1.key,
                    scene2.key,
                    transition1
                )
            )
        )

        /* When */
        val result = factory.supports(scene2, scene3, null)

        /* Then */
        expect(result).toBe(false)
    }
}