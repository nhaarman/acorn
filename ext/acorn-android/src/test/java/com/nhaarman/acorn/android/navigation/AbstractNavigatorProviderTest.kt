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

package com.nhaarman.acorn.android.navigation

import com.nhaarman.acorn.navigation.TestNavigator
import com.nhaarman.acorn.state.NavigatorState
import com.nhaarman.expect.expect
import org.junit.jupiter.api.Test

internal class AbstractNavigatorProviderTest {

    private val navigatorProvider = TestAbstractNavigatorProvider()

    @Test
    fun `navigator is cached`() {
        /* When */
        val result1 = navigatorProvider.navigatorFor(null)
        val result2 = navigatorProvider.navigatorFor(null)

        /* Then */
        expect(result1).toBeTheSameAs(result2)
    }

    @Test
    fun `destroyed navigator is renewed`() {
        /* Given */
        val result1 = navigatorProvider.navigatorFor(null)
        result1.onDestroy()

        /* When */
        val result2 = navigatorProvider.navigatorFor(null)

        /* Then */
        expect(result1).toNotBeTheSameAs(result2)
    }

    private class TestAbstractNavigatorProvider : AbstractNavigatorProvider<TestNavigator>() {

        override fun createNavigator(savedState: NavigatorState?): TestNavigator {
            return TestNavigator()
        }
    }
}
