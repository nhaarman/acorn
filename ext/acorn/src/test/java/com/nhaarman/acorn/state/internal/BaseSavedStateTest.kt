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

package com.nhaarman.bravo.state.internal

import com.nhaarman.bravo.state.SavedState
import com.nhaarman.bravo.state.get
import com.nhaarman.bravo.state.savedState
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

internal class BaseSavedStateTest {

    val state = BaseSavedState()

    @Test
    fun `missing String value`() {
        /* Given */
        state.clear("key")

        /* When */
        val result: String? = state["key"]

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `valid String value`() {
        /* Given */
        state["key"] = "Hello!"

        /* When */
        val result: String? = state["key"]

        /* Then */
        expect(result).toBe("Hello!")
    }

    @Test
    fun `retrieving String value from Int`() {
        /* Given */
        state["key"] = 3

        /* When */
        val result: String? = state["key"]

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `valid Boolean value`() {
        /* Given */
        state["key"] = true

        /* When */
        val result: Boolean? = state["key"]

        /* Then */
        expect(result).toBe(true)
    }

    @Test
    fun `valid Byte value`() {
        /* Given */
        state["key"] = 3

        /* When */
        val result: Byte? = state["key"]

        /* Then */
        expect(result).toBe(3)
    }

    @Test
    fun `valid Char value`() {
        /* Given */
        state["key"] = 'a'

        /* When */
        val result: Char? = state["key"]

        /* Then */
        expect(result).toBe('a')
    }

    @Test
    fun `valid Double value`() {
        /* Given */
        state["key"] = 3.0

        /* When */
        val result: Double? = state["key"]

        /* Then */
        expect(result).toBe(3.0)
    }

    @Test
    fun `valid Float value`() {
        /* Given */
        state["key"] = 3f

        /* When */
        val result: Float? = state["key"]

        /* Then */
        expect(result).toBe(3f)
    }

    @Test
    fun `valid Int value`() {
        /* Given */
        state["key"] = 3

        /* When */
        val result: Int? = state["key"]

        /* Then */
        expect(result).toBe(3)
    }

    @Test
    fun `valid Long value`() {
        /* Given */
        state["key"] = 3L

        /* When */
        val result: Long? = state["key"]

        /* Then */
        expect(result).toBe(3L)
    }

    @Test
    fun `valid Short value`() {
        /* Given */
        state["key"] = 3

        /* When */
        val result: Short? = state["key"]

        /* Then */
        expect(result).toBe(3)
    }

    @Test
    fun `retrieving Int value from String`() {
        /* Given */
        state["key"] = 3

        /* When */
        val result: String? = state["key"]

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `valid SavedState`() {
        /* Given */
        state["key"] = savedState { it["foo"] = 3 }

        /* When */
        val result: SavedState? = state["key"]

        /* Then */
        expect(result).toBe(savedState { it["foo"] = 3 })
    }
}