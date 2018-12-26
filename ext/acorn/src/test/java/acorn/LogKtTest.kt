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