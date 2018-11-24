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

package com.nhaarman.acorn.android.uistate

import com.nhaarman.acorn.android.uistate.internal.Destination
import com.nhaarman.acorn.android.util.RootViewGroup
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.android.util.TestTransitionFactory
import com.nhaarman.acorn.android.util.TestView
import com.nhaarman.acorn.android.util.TestViewController
import com.nhaarman.acorn.android.util.TestViewControllerFactory
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NotVisibleWithDestinationTest {

    val root = spy(RootViewGroup())
    val scene = spy(TestScene())
    val sceneView = TestView()
    val viewController = TestViewController(sceneView)
    val viewControllerFactory = TestViewControllerFactory()
    val destination = Destination(
        scene,
        viewControllerFactory,
        null
    )

    val state = NotVisibleWithDestination(
        root,
        TestTransitionFactory(),
        destination,
        null
    )

    @BeforeEach
    fun setup() {
        viewControllerFactory.register(scene.key, viewController)
    }

    @Test
    fun `'uiNotVisible' makes no transition`() {
        expect(state.uiNotVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' results in NotVisible state`() {
        expect(state.withoutScene()).toBeInstanceOf<NotVisible>()
    }

    @Test
    fun `'withoutScene' does not touch root view group`() {
        /* When */
        state.withoutScene()

        /* Then */
        verifyZeroInteractions(root)
    }

    @Test
    fun `'withScene' results in NotVisibleWithDestination state`() {
        expect(state.withScene(mock(), mock(), null)).toBeInstanceOf<NotVisibleWithDestination>()
    }

    @Test
    fun `'withScene' does not touch root view group`() {
        /* When */
        state.withScene(mock(), mock(), null)

        /* Then */
        verifyZeroInteractions(root)
    }

    @Test
    fun `'uiVisible' results in VisibleWithDestination state`() {
        expect(state.uiVisible()).toBeInstanceOf<VisibleWithDestination>()
    }

    @Test
    fun `'uiVisible' replaces root children with new Scene view`() {
        /* When */
        state.uiVisible()

        /* Then */
        expect(root.views).toBe(listOf(sceneView))
    }

    @Test
    fun `'uiVisible' attaches container to scene`() {
        /* When */
        state.uiVisible()

        /* Then */
        verify(scene).attach(any())
    }

    @Test
    fun `'uiVisible' for state with existing view controller does not manipulate root`() {
        /* Given */
        val existingView = TestView()
        root.addView(existingView)

        val state = NotVisibleWithDestination(
            root,
            TestTransitionFactory(),
            destination,
            TestViewController(TestView())
        )

        /* When */
        state.uiVisible()

        /* Then */
        expect(root.views).toBe(listOf(existingView))
    }

    @Test
    fun `'uiVisible' for state with existing view controller attaches that controller`() {
        /* Given */
        val viewController = TestViewController(TestView())
        val state = NotVisibleWithDestination(
            root,
            TestTransitionFactory(),
            destination,
            viewController
        )

        /* When */
        state.uiVisible()

        /* Then */
        verify(scene).attach(viewController)
    }
}