/*
 * Bravo - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Bravo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bravo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bravo.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.bravo.android.uistate

import com.nhaarman.bravo.android.util.RootViewGroup
import com.nhaarman.bravo.android.util.TestScene
import com.nhaarman.bravo.android.util.TestTransitionFactory
import com.nhaarman.bravo.android.util.TestView
import com.nhaarman.bravo.android.util.TestViewController
import com.nhaarman.bravo.android.util.TestViewControllerProvider
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

internal class VisibleTest {

    val root = spy(RootViewGroup())
    val scene = spy(TestScene())
    val sceneView = TestView()
    val sceneViewControllerProvider = TestViewControllerProvider(TestViewController(sceneView))

    val state = Visible(
        root,
        TestTransitionFactory()
    )

    @Test
    fun `'uiVisible' makes no transition`() {
        expect(state.uiVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' makes no transition`() {
        expect(state.withoutScene()).toBe(state)
    }

    @Test
    fun `'uiNotVisible' results in NotVisible state`() {
        expect(state.uiNotVisible()).toBeInstanceOf<NotVisible>()
    }

    @Test
    fun `'withScene' results in VisibleWithDestination state`() {
        expect(state.withScene(scene, sceneViewControllerProvider, null)).toBeInstanceOf<VisibleWithDestination>()
    }

    @Test
    fun `'withScene' replaces root children with new Scene view`() {
        /* When */
        state.withScene(scene, sceneViewControllerProvider, null)

        /* Then */
        expect(root.views).toBe(listOf(sceneView))
    }

    @Test
    fun `'withScene' attaches container to scene`() {
        /* When */
        state.withScene(scene, sceneViewControllerProvider, null)

        /* Then */
        verify(scene).attach(any())
    }
}