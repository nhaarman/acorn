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

package com.nhaarman.acorn.state.internal

import com.nhaarman.acorn.state.SavedState
import com.nhaarman.acorn.state.get
import com.nhaarman.acorn.state.savedState
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
    fun `clearing value`() {
        /* Given */
        state["key"] = "test"
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

    @Test
    fun `entries contains proper values`() {
        /* Given */
        state["key1"] = "test"
        state["key2"] = 3

        /* When */
        val result = state.entries

        /* Then */
        expect(result).toBe(
            mapOf(
                "key1" to "test",
                "key2" to 3
            ).entries
        )
    }
}