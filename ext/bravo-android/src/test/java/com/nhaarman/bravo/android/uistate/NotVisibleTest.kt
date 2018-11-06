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
import com.nhaarman.bravo.android.util.TestTransitionFactory
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Test

internal class NotVisibleTest {

    val root = spy(RootViewGroup())

    val state = NotVisible(
        root,
        TestTransitionFactory()
    )

    @Test
    fun `'uiNotVisible' makes no transition`() {
        expect(state.uiNotVisible()).toBe(state)
    }

    @Test
    fun `'withoutScene' makes no transition`() {
        expect(state.withoutScene()).toBe(state)
    }

    @Test
    fun `'uiVisible' results in Visible state`() {
        expect(state.uiVisible()).toBeInstanceOf<Visible>()
    }

    @Test
    fun `'withScene' results in NotVisibleWithDestination state`() {
        expect(state.withScene(mock(), mock(), null)).toBeInstanceOf<NotVisibleWithDestination>()
    }

    @Test
    fun `'withScene' does not touch root viewgroup`() {
        /* When */
        state.withScene(mock(), mock(), null)

        /* Then */
        verifyZeroInteractions(root)
    }
}