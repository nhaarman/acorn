/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
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
            activityHandler.onActivityResult(3, null)

            /* Then */
            inOrder(scene, activityController) {
                verify(scene).attach(activityController)
                verify(activityController).onResult(3, null)
                verify(scene).detach(activityController)
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