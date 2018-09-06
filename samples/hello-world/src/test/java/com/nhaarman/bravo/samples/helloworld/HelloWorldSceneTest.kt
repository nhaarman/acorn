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

package com.nhaarman.bravo.samples.helloworld

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

class HelloWorldSceneTest {

    private val scene = HelloWorldScene()

    @Test
    fun `attaching container shows "Hello, world!" text`() {
        /* Given */
        val container = TestContainer()

        /* When */
        scene.attach(container)

        /* Then */
        expect(container.text).toBe("Hello, world!")
    }

    private class TestContainer : HelloWorldContainer {

        override var text: String = ""
    }
}