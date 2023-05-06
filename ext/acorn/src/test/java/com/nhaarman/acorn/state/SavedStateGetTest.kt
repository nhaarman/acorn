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

package com.nhaarman.acorn.state

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

internal class SavedStateGetTest {

    @Test
    fun `retrieving a byte`() {
        // Given
        val value: Byte = 3
        val state = savedState {
            it["value"] = value
        }

        // When
        val result: Byte? = state["value"]

        // Then
        expect(result).toBe(value)
    }

    @Test
    fun `retrieving a byte entered as int`() {
        // Given
        val value: Int = 3
        val state = savedState {
            it["value"] = value
        }

        // When
        val result: Byte? = state["value"]

        // Then
        expect(result).toBe(3)
    }

    @Test
    fun `retrieving a short`() {
        // Given
        val value: Short = 3
        val state = savedState {
            it["value"] = value
        }

        // When
        val result: Short? = state["value"]

        // Then
        expect(result).toBe(value)
    }

    @Test
    fun `retrieving an unchecked item`() {
        // Given
        val value = "Test"
        val state = savedState {
            it["value"] = value
        }

        // When
        val result: String? = state["value"]

        // Then
        expect(result).toBe("Test")
    }
}
