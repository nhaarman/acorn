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