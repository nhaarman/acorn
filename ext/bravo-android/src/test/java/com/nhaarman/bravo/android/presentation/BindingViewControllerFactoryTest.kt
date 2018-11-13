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
import com.nhaarman.bravo.android.presentation.internal.BindingViewControllerFactory
import com.nhaarman.bravo.presentation.SceneKey
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class BindingViewControllerFactoryTest {

    @Test
    fun `empty factory does not support`() {
        /* Given */
        val factory = BindingViewControllerFactory(emptyMap())

        /* When */
        val result = factory.supports(SceneKey("test"))

        /* Then */
        expect(result).toBe(false)
    }

    @Test
    fun `empty factory throws for non-supported key`() {
        /* Given */
        val factory = BindingViewControllerFactory(emptyMap())

        /* Expect */
        expectErrorWithMessage("Could not create") on {

            /* When */
            factory.viewControllerFor(SceneKey("test"), mock())
        }
    }

    @Test
    fun `factory with missing key throws`() {
        /* Given */
        val factory = BindingViewControllerFactory(
            mapOf(SceneKey("1") to MyViewControllerFactory())
        )

        /* Expect */
        expectErrorWithMessage("Could not create") on {

            /* When */
            factory.viewControllerFor(SceneKey("2"), mock())
        }
    }

    @Test
    fun `proper result`() {
        /* Given */
        val myViewCreator = MyViewControllerFactory()
        val factory = BindingViewControllerFactory(
            mapOf(SceneKey("1") to myViewCreator)
        )

        /* When */
        val result = factory.viewControllerFor(SceneKey("1"), mock())

        /* Then */
        expect(result).toBe(myViewCreator.result)
    }

    class MyViewControllerFactory : ViewControllerFactory {

        var result: ViewController = mock()

        override fun supports(sceneKey: SceneKey): Boolean {
            return true
        }

        override fun viewControllerFor(sceneKey: SceneKey, parent: ViewGroup): ViewController {
            return result
        }
    }
}