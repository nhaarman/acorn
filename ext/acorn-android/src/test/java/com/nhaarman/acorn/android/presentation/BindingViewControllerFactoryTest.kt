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

package com.nhaarman.acorn.android.presentation

import android.view.ViewGroup
import com.nhaarman.acorn.android.presentation.internal.BindingViewControllerFactory
import com.nhaarman.acorn.android.util.TestScene
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

internal class BindingViewControllerFactoryTest {

    val scene1 = TestScene.withKey("1")
    val scene2 = TestScene.withKey("2")

    @Test
    fun `empty factory does not support`() {
        /* Given */
        val factory = BindingViewControllerFactory(emptyMap())

        /* When */
        val result = factory.supports(scene1)

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
            factory.viewControllerFor(scene1, mock())
        }
    }

    @Test
    fun `factory with missing key throws`() {
        /* Given */
        val factory = BindingViewControllerFactory(
            mapOf(scene1.key to MyViewControllerFactory())
        )

        /* Expect */
        expectErrorWithMessage("Could not create") on {

            /* When */
            factory.viewControllerFor(scene2, mock())
        }
    }

    @Test
    fun `proper result`() {
        /* Given */
        val myViewCreator = MyViewControllerFactory()
        val factory = BindingViewControllerFactory(
            mapOf(scene1.key to myViewCreator)
        )

        /* When */
        val result = factory.viewControllerFor(scene1, mock())

        /* Then */
        expect(result).toBe(myViewCreator.result)
    }

    class MyViewControllerFactory : ViewControllerFactory {

        var result: ViewController = mock()

        override fun supports(scene: Scene<*>): Boolean {
            return true
        }

        override fun viewControllerFor(scene: Scene<*>, parent: ViewGroup): ViewController {
            return result
        }
    }
}