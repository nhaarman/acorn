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

package acorn

import com.nhaarman.acorn.Logger
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class LogKtTest {

    @Test
    fun `setting the logger`() {
        /* Given */
        val instance = mock<Logger>()

        /* When */
        acorn.logger = instance

        /* Then */
        expect(com.nhaarman.acorn.logger).toBe(instance)
    }

    @Test
    fun `reading the logger`() {
        /* Given */
        val instance = mock<Logger>()

        /* When */
        com.nhaarman.acorn.logger = instance

        /* Then */
        expect(acorn.logger).toBe(instance)
    }
}