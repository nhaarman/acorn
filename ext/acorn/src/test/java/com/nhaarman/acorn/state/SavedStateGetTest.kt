package com.nhaarman.acorn.state

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

internal class SavedStateGetTest {

    @Test
    fun `retrieving a byte`() {
        /* Given */
        val value: Byte = 3
        val state = savedState {
            it["value"] = value
        }

        /* When */
        val result: Byte? = state["value"]

        /* Then */
        expect(result).toBe(value)
    }

    @Test
    fun `retrieving a byte entered as int`() {
        /* Given */
        val value: Int = 3
        val state = savedState {
            it["value"] = value
        }

        /* When */
        val result: Byte? = state["value"]

        /* Then */
        expect(result).toBe(3)
    }

    @Test
    fun `retrieving a short`() {
        /* Given */
        val value: Short = 3
        val state = savedState {
            it["value"] = value
        }

        /* When */
        val result: Short? = state["value"]

        /* Then */
        expect(result).toBe(value)
    }

    @Test
    fun `retrieving an unchecked item`() {
        /* Given */
        val value = "Test"
        val state = savedState {
            it["value"] = value
        }

        /* When */
        val result: String? = state["value"]

        /* Then */
        expect(result).toBe("Test")
    }
}