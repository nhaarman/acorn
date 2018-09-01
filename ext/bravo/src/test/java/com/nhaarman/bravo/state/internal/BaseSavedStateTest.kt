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