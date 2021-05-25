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

package com.nhaarman.acorn.android.dispatching.internal

import android.content.Intent
import com.nhaarman.acorn.android.presentation.ActivityController
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultActivityHandlerTest {

    private val callback = mock<DefaultActivityHandler.Callback>()

    private val scene = spy(TestScene.withKey("scene_1"))
    private val scene2 = TestScene.withKey("scene_2")

    private val intent = Intent()
    private val activityController = spy(TestActivityController(intent))

    private val intent2 = Intent()
    private val activityController2 = TestActivityController(intent2)

    @Nested
    inner class StartingScenesWithoutSavedState {

        private val activityHandler = DefaultActivityHandler(
            callback,
            null
        )

        @Test
        fun `withScene starts Intent`() {
            /* When */
            activityHandler.withScene(scene, activityController)

            /* Then */
            verify(callback).startForResult(intent)
        }

        @Test
        fun `withoutScene does not start Intent`() {
            /* When */
            activityHandler.withoutScene()

            /* Then */
            verify(callback, never()).startForResult(any())
        }

        @Test
        fun `withScene twice with the same scene starts Intent only once`() {
            /* When */
            activityHandler.withScene(scene, activityController)
            activityHandler.withScene(scene, activityController)

            /* Then */
            verify(callback, times(1)).startForResult(intent)
        }

        @Test
        fun `withScene twice with different scene starts Intent twice`() {
            /* When */
            activityHandler.withScene(scene, activityController)
            activityHandler.withScene(scene2, activityController2)

            /* Then */
            callback.inOrder {
                verify().startForResult(intent)
                verify().startForResult(intent2)
            }
        }

        @Test
        fun `withScene allows starting twice with different scene in between`() {
            /* When */
            activityHandler.withScene(scene, activityController)
            activityHandler.withScene(scene2, activityController2)
            activityHandler.withScene(scene, activityController)

            /* Then */
            callback.inOrder {
                verify().startForResult(intent)
                verify().startForResult(intent2)
                verify().startForResult(intent)
            }
        }

        @Test
        fun `withScene allows starting twice with no scene in between`() {
            /* When */
            activityHandler.withScene(scene, activityController)
            activityHandler.withoutScene()
            activityHandler.withScene(scene, activityController)

            /* Then */
            verify(callback, times(2)).startForResult(intent)
        }
    }

    @Nested
    inner class StartingScenesWithSavedState {

        private val activityHandler by lazy {
            val original = DefaultActivityHandler(mock(), null)
            original.withScene(scene, activityController)
            val state = original.saveInstanceState()
            DefaultActivityHandler(callback, state)
        }

        @Test
        fun `withScene for same scene does not start Intent`() {
            /* When */
            activityHandler.withScene(scene, activityController)

            /* Then */
            verify(callback, never()).startForResult(any())
        }

        @Test
        fun `withScene for different scene does start Intent`() {
            /* When */
            activityHandler.withScene(scene2, activityController2)

            /* Then */
            verify(callback).startForResult(intent2)
        }

        @Test
        fun `withoutScene does not start Intent`() {
            /* When */
            activityHandler.withoutScene()

            /* Then */
            verify(callback, never()).startForResult(any())
        }

        @Test
        fun `withScene twice with different second scene only starts Intent once`() {
            /* When */
            activityHandler.withScene(scene, activityController)
            activityHandler.withScene(scene2, activityController2)

            /* Then */
            callback.inOrder {
                verify().startForResult(intent2)
            }
        }

        @Test
        fun `withScene allows starting saved scene after different scene`() {
            /* When */
            activityHandler.withScene(scene2, activityController2)
            activityHandler.withScene(scene, activityController)

            /* Then */
            callback.inOrder {
                verify().startForResult(intent2)
                verify().startForResult(intent)
            }
        }

        @Test
        fun `withScene allows starting saved scene after no scene`() {
            /* When */
            activityHandler.withoutScene()
            activityHandler.withScene(scene, activityController)

            /* Then */
            verify(callback).startForResult(intent)
        }
    }

    @Nested
    inner class Results {

        private val activityHandler = DefaultActivityHandler(
            callback,
            null
        )

        @Test
        fun `onActivityResult attaches and notifies activityController`() {
            /* Given */
            activityHandler.withScene(scene, activityController)

            /* When */
            activityHandler.onActivityResult(42, 3, null)

            /* Then */
            inOrder(scene, activityController) {
                verify(scene).attach(activityController)
                verify(activityController).onResult(3, null)
                verify(scene).detach(activityController)
            }
        }

        @Nested
        inner class ResultsWithSavedState {

            private val activityHandler by lazy {
                val original = DefaultActivityHandler(mock(), null)
                original.withScene(scene, activityController)
                val state = original.saveInstanceState()
                DefaultActivityHandler(callback, state)
            }

            @Test
            // https://github.com/nhaarman/acorn/issues/165
            fun `onActivityResult notifies activityController for restored state`() {
                /* Given */
                activityHandler.withScene(scene, activityController)

                /* When */
                activityHandler.onActivityResult(42, 3, null)

                /* Then */
                inOrder(scene, activityController) {
                    verify(scene).attach(activityController)
                    verify(activityController).onResult(3, null)
                    verify(scene).detach(activityController)
                }
            }
        }
    }

    private class TestActivityController(private val intent: Intent) : ActivityController {

        override fun createIntent(): Intent {
            return intent
        }

        override fun onResult(resultCode: Int, data: Intent?) {
        }
    }
}
