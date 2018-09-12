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

package com.nhaarman.bravo.android.presentation

import android.view.ViewGroup
import com.nhaarman.bravo.android.presentation.internal.BindingViewFactory
import com.nhaarman.bravo.android.presentation.internal.ViewCreator
import com.nhaarman.bravo.presentation.SceneKey
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class BindingViewFactoryTest {

    @Test
    fun `empty factory`() {
        /* Given */
        val factory = BindingViewFactory(emptyMap())

        /* When */
        val result = factory.viewFor(SceneKey("test"), mock())

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `factory with missing key`() {
        /* Given */
        val factory = BindingViewFactory(
            mapOf(SceneKey("1") to MyViewCreator())
        )

        /* When */
        val result = factory.viewFor(SceneKey("2"), mock())

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `proper result`() {
        /* Given */
        val myViewCreator = MyViewCreator()
        val factory = BindingViewFactory(
            mapOf(SceneKey("1") to myViewCreator)
        )

        /* When */
        val result = factory.viewFor(SceneKey("1"), mock())

        /* Then */
        expect(result).toBe(myViewCreator.result)
    }

    class MyViewCreator : ViewCreator {

        var result: ViewResult = mock()

        override fun create(parent: ViewGroup): ViewResult {
            return result
        }
    }
}